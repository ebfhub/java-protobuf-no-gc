package org.ebfhub.fastprotobuf;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.SecureClassLoader;
import java.util.*;

/**
 * MASSIVELY based on http://javapracs.blogspot.de/2011/06/dynamic-in-memory-compilation-using.html by Rekha Kumari
 * (June 2011)
 */
final public class InMemoryCompiler {

    final public static class IMCSourceCode {

        final public String fullClassName;
        final public String sourceCode;

        /**
         * @param fullClassName Full name of the class that will be compiled. If the class should be in some package,
         *                      fullName should contain it too, for example: "testpackage.DynaClass"
         * @param sourceCode    the source code
         */
        public IMCSourceCode(final String fullClassName, final String sourceCode) {

            this.fullClassName = fullClassName;
            this.sourceCode = sourceCode;
        }
    }

    final public boolean valid;

    final private List<IMCSourceCode> classSourceCodes;
    final private JavaFileManager fileManager;

    public InMemoryCompiler(final List<IMCSourceCode> classSourceCodes) {

        this.classSourceCodes = classSourceCodes;

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            fileManager = null;
            valid = false;
            System.err.println("ToolProvider.getSystemJavaCompiler() returned null! This program needs to be run on a system with an installed JDK.");
            return;
        }
        valid = true;

        fileManager = new ForwardingJavaFileManager<JavaFileManager>(compiler.getStandardFileManager(null, null, null)) {

            final private Map<String, ByteArrayOutputStream> byteStreams = new HashMap<>();

            @Override
            public ClassLoader getClassLoader(final Location location) {

                return new SecureClassLoader() {

                    @Override
                    protected Class<?> findClass(final String className) {

                        final ByteArrayOutputStream bos = byteStreams.get(className);
                        if (bos == null) {
                            return null;
                        }
                        final byte[] b = bos.toByteArray();
                        return super.defineClass(className, b, 0, b.length);
                    }
                };
            }

            @Override
            public JavaFileObject getJavaFileForOutput(final Location location, final String className, final JavaFileObject.Kind kind, final FileObject sibling) throws IOException {

                return new SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind) {

                    @Override
                    public OutputStream openOutputStream() {

                        ByteArrayOutputStream bos = byteStreams.get(className);
                        if (bos == null) {
                            bos = new ByteArrayOutputStream();
                            byteStreams.put(className, bos);
                        }
                        return bos;
                    }
                };
            }
        };
    }

    public CompilerFeedback compile() {

        if (!valid) {
            return null;
        }
        final List<JavaFileObject> files = new ArrayList<>();
        for (IMCSourceCode classSourceCode : classSourceCodes) {
            URI uri = null;
            try {
                uri = URI.create("string:///" + classSourceCode.fullClassName.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (uri != null) {
                final SimpleJavaFileObject sjfo = new SimpleJavaFileObject(uri, JavaFileObject.Kind.SOURCE) {

                    @Override
                    public CharSequence getCharContent(final boolean ignoreEncodingErrors) {

                        return classSourceCode.sourceCode;
                    }
                };
                files.add(sjfo);
            }
        }

        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        if (files.size() > 0) {
            final JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, files);
            return new CompilerFeedback(task.call(), diagnostics);
        } else {
            return null;
        }
    }

    public Class<?> getCompiledClass(final String className) throws ClassNotFoundException {

        if (!valid) {
            throw new IllegalStateException("InMemoryCompiler instance not usable because ToolProvider.getSystemJavaCompiler() returned null: No JDK installed.");
        }
        final ClassLoader classLoader = fileManager.getClassLoader(null);
        final Class<?> ret = classLoader.loadClass(className);
        if (ret == null) {
            throw new ClassNotFoundException("Class returned by ClassLoader was null!");
        }
        return ret;
    }


    static final public class CompilerFeedback {

        final public boolean success;
        final public List<CompilerMessage> messages = new ArrayList<>();

        public CompilerFeedback(final Boolean success, final DiagnosticCollector<JavaFileObject> diagnostics) {

            this.success = success != null && success;
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                messages.add(new CompilerMessage(diagnostic));
            }
        }

        public String toString() {

            final StringBuilder sb = new StringBuilder();

            sb.append("SUCCESS: ").append(success).append('\n');
            final int iTop = messages.size();
            for (int i = 0; i < iTop; i++) {
                sb.append("\n[MESSAGE ").append(i + 1).append(" OF ").append(iTop).append("]\n\n");
                sb.append(messages.get(i).toStringForDebugging()).append("\n");
            }
            return sb.toString();
        }

        final public static class CompilerMessage {

            final public Diagnostic<? extends JavaFileObject> compilerInfo;

            final public String typeOfProblem;
            final public String typeOfProblem_forDebugging;

            final public String multiLineMessage;

            final public int lineNumber;
            final public int columnNumber;

            final public int textHighlightPos_lineStart;
            final public int textHighlightPos_problemStart;
            final public int textHighlightPos_problemEnd;

            final public String sourceCode;
            final public String codeOfConcern;
            final public String codeOfConcernLong;

            CompilerMessage(final Diagnostic<? extends JavaFileObject> diagnostic) {

                final JavaFileObject sourceFileObject = diagnostic.getSource();
                String sourceCodePreliminary = null;
                if (sourceFileObject instanceof SimpleJavaFileObject) {
                    final SimpleJavaFileObject simpleSourceFileObject = (SimpleJavaFileObject) sourceFileObject;

                    try {
                        final CharSequence charSequence = simpleSourceFileObject.getCharContent(false);
                        sourceCodePreliminary = charSequence.toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sourceCodePreliminary == null) {
                    sourceCode = "[SOURCE CODE UNAVAILABLE]";
                } else {
                    sourceCode = sourceCodePreliminary;
                }

                compilerInfo = diagnostic;

                typeOfProblem = diagnostic.getKind().name();
                typeOfProblem_forDebugging = "toString() = " + diagnostic.getKind().toString() + "; name() = " + typeOfProblem;

                lineNumber = (int) compilerInfo.getLineNumber();
                columnNumber = (int) compilerInfo.getColumnNumber();

                final int sourceLen = sourceCode.length();
                textHighlightPos_lineStart = (int) Math.min(Math.max(0, diagnostic.getStartPosition()), sourceLen);
                textHighlightPos_problemStart = (int) Math.min(Math.max(0, diagnostic.getPosition()), sourceLen);
                textHighlightPos_problemEnd = (int) Math.min(Math.max(0, diagnostic.getEndPosition()), sourceLen);

                final StringBuilder reformattedMessage = new StringBuilder();
                final String message = diagnostic.getMessage(Locale.US);
                final int messageCutOffPosition = message.indexOf("location:");
                final String[] messageParts;
                if (messageCutOffPosition >= 0) {
                    messageParts = message.substring(0, messageCutOffPosition).split("\n");
                } else {
                    messageParts = message.split("\n");
                }
                for (String s : messageParts) {
                    String s2 = s.trim();
                    if (s2.length() > 0) {
                        boolean lengthChanged;
                        do {
                            final int lBeforeReplace = s2.length();
                            s2 = s2.replace("  ", " ");
                            lengthChanged = (s2.length() != lBeforeReplace);
                        } while (lengthChanged);
                        reformattedMessage.append(s2).append("\n");
                    }
                }

                codeOfConcern = sourceCode.substring(textHighlightPos_problemStart, textHighlightPos_problemEnd);
                codeOfConcernLong = sourceCode.substring(textHighlightPos_lineStart, textHighlightPos_problemEnd);
                if (!codeOfConcern.isEmpty()) {
                    reformattedMessage.append("Code of concern: \"").append(codeOfConcern).append('\"');
                }
                multiLineMessage = reformattedMessage.toString();
            }

            public String toStringForList() {

                if (compilerInfo == null) {
                    return "No compiler!";
                } else {
                    return compilerInfo.getCode();
                }
            }

            public String toStringForDebugging() {

                final StringBuilder ret = new StringBuilder();

                ret.append("Type of problem: ").append(typeOfProblem_forDebugging).append("\n\n");
                ret.append("Message:\n").append(multiLineMessage).append("\n\n");

                ret.append(compilerInfo.getCode()).append("\n\n");

                ret.append("line number: ").append(lineNumber).append("\n");
                ret.append("column number: ").append(columnNumber).append("\n");

                ret.append("textHighlightPos_lineStart: ").append(textHighlightPos_lineStart).append("\n");
                ret.append("textHighlightPos_problemStart: ").append(textHighlightPos_problemStart).append("\n");
                ret.append("textHighlightPos_problemEnd: ").append(textHighlightPos_problemEnd).append("\n");

                return ret.toString();
            }

            @Override
            public String toString() {

                //            return compilerInfo.getMessage(Locale.US);
                return typeOfProblem + ": " + multiLineMessage + "\n";
            }
        }
    }
}