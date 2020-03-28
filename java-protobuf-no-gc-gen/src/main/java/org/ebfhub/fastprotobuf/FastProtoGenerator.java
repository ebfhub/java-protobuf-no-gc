package org.ebfhub.fastprotobuf;

import com.google.common.base.Strings;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.WireFormat;
import com.google.protobuf.compiler.PluginProtos;
import com.salesforce.jprotoc.Generator;
import com.salesforce.jprotoc.GeneratorException;
import com.salesforce.jprotoc.ProtocPlugin;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>FastProtoGenerator class.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public class FastProtoGenerator extends Generator {
    private final boolean debug;

    /**
     * <p>Constructor for FastProtoGenerator.</p>
     *
     * @param debug a boolean.
     */
    public FastProtoGenerator(boolean debug) {
        this.debug=debug;
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            // Generate from protoc via stdin
            ProtocPlugin.generate(new FastProtoGenerator(false));
        } else {
            // Process from a descriptor_dump file via command line arg
            ProtocPlugin.debug(new FastProtoGenerator(true), args[0]);
        }
    }


    static final String CLASS_SUFFIX="Fast";
    Map<String,String> pNameToName=new HashMap<>();
    String poolClassName = FastProtoObjectPool.class.getName().replaceAll("[$]", ".");


    /** {@inheritDoc} */
    @Override
    public List<PluginProtos.CodeGeneratorResponse.File> generateFiles(PluginProtos.CodeGeneratorRequest request) throws GeneratorException {

        List<PluginProtos.CodeGeneratorResponse.File> files = new ArrayList<>();


        for (DescriptorProtos.FileDescriptorProto protoFile : request.getProtoFileList()) {
            if (request.getFileToGenerateList().contains(protoFile.getName())) {

                String className = protoFile.getOptions().getJavaOuterClassname()+ CLASS_SUFFIX;
                this.javaClassName=className;
                this.javaPackage = protoFile.getOptions().getJavaPackage();

                JavaOutput sb = new JavaOutput();

                sb.line("package "+protoFile.getOptions().getJavaPackage()+";");
                sb.blank();
                sb.imports(
                     com.google.protobuf.WireFormat.class,
                     com.google.protobuf.CodedOutputStream.class
                );

                sb.blank();

                String fileName = className+ ".java";

                sb.blank();
                sb.line("@SuppressWarnings({\"unused\",\"SwitchStatementWithTooFewBranches\",\"ForLoopReplaceableByForEach\",\"UnusedReturnValue\",\"ArraysAsListWithZeroOrOneArgument\"})");
                sb.blank();
                sb.line("public class "+className+" {");

                for(DescriptorProtos.DescriptorProto pp : protoFile.getMessageTypeList()){

                    pNameToName.put("."+protoFile.getPackage()+"."+pp.getName(),pp.getName());
                }

                for(DescriptorProtos.DescriptorProto pp : protoFile.getMessageTypeList()){

                    ClassInfo info = calcClassInfo(pp);
                    String thisClass = info.className;

                    sb.line("public static class "+thisClass+" implements "+
                            FastProtoMessage.class.getName()+","+
                            FastProtoWritable.class.getName()+"{");

                    sb.line("private "+poolClassName+" pool;");
                    // Create fields
                    for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()){

                        TypeInfo ti = getJavaTypeInfo(field);
                        sb.line("private " + getJavaTypeName(ti, false) + " " + field.getName() + ";");

                    }
                    sb.blank();
                    sb.blank();
                    sb.line("private int "+info.fieldSetVar+"=0;");
                    sb.blank();


                    sb.line("private "+ thisClass+" ("+poolClassName+" pool){");
                    sb.line("this.pool=pool;");
                    sb.line("}");
                    sb.line("public static "+ thisClass+" create("+poolClassName+" pool){");
                    sb.line("return new "+thisClass+"(pool);");
                    sb.line("}");
                    sb.line("public "+poolClassName+" getPool(){");
                    sb.line("return this.pool;");
                    sb.line("}");

                    generateFieldDefStatics(sb, pp, info);


                    for(OneOf e:info.oneOfs.values()){
                        sb.line("public enum "+e.className+"{");
                        for(String k:e.fields){
                            sb.line(k+",");
                        }
                        sb.line("}\n");
                        sb.line("private "+e.className+" "+e.valueName+"=null;");

                    }

                    for(OneOf e:info.oneOfs.values()){
                        sb.line("public "+e.className+" get"+e.className+"(){");
                        sb.line("return "+e.valueName+";");
                        sb.line("}\n");
                    }

                    sb.line("public boolean isSet("+FastProtoField.class.getName()+" f){");
                    sb.line("return ("+info.fieldSetVar+" & f.bit)!=0;");
                    sb.line("}");
                    sb.blank();


                    generateToString(sb, pp);
                    generateClear(sb, pp, info);

                    generateWrite(sb, pp);

                    sb.blank();
                    addParseMethodHelpers(sb, pp, info);
                    for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()){
                        generatePublicFieldAccessMethods(sb, info, thisClass, field);
                    }
                    sb.blank();
                    sb.line("}");
                }
                sb.line("}");

                String packageName = extractPackageName(protoFile);

                String output=sb.toString();

                files.add(buildFile(packageName,fileName,output));

                if(debug) {
                    //System.out.println(output);
                    testOutput=output;

                }
            }
        }

        return files;
    }

    private void generateFieldDefStatics(JavaOutput sb, DescriptorProtos.DescriptorProto pp, ClassInfo info) {
        sb.line("private static class FieldNum {");
        for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()){
            sb.line("static final int "+field.getName()+"="+field.getNumber()+";");
        }
        sb.line("}");

        sb.line("private static class FieldBit {");
        for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()){
            sb.line("static final int "+field.getName()+"="+info.bits.get(field.getName())+";");
        }
        sb.line("}");
        sb.blank();
        sb.line("public static class Field {");

        for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()){
            TypeInfo ti = getJavaTypeInfo(field);
            sb.line("public static "+ FastProtoField.class.getName()+" "+field.getName()+
                    "=new "+FastProtoField.class.getName()+"(\""+field.getName()+"\",FieldNum."+field.getName()+",FieldBit."+field.getName()+
                    ",WireFormat.FieldType."+wireTypes.get(field.getType())+
                    ","+
                    ti.repeated +
                    ","+
                    (ti.typeName==null?null:ti.typeName+".class")+
                    ");");
        }

        sb.line("}");
        sb.blank();
    }

    private void generatePublicFieldAccessMethods(JavaOutput sb, ClassInfo info, String thisClass, DescriptorProtos.FieldDescriptorProto field) {
        // GETTER
        TypeInfo ti = getJavaTypeInfo(field);
        sb.line("public " + getJavaTypeName(ti, true, ti.repeated)+ " get" + upperCaseName(field.getName())+ "() {");

        sb.line("if((this." +info.fieldSetVar + "&" + info.bits.get(field.getName()) + ")!=0){");

        sb.line("return this." + field.getName() + ";");
        sb.line("} else {");
        if(ti.type==TypeInfoType.STRING&&!ti.repeated) {

            sb.line("return \"\";");  // Java version returns "" if string unset
        } else if(isMutableOrList(ti)) {

            sb.line("return null;");
        } else if (ti.type== TypeInfoType.BOOL){
            sb.line("return false;");
        } else {
            sb.line("return 0;");
        }
        sb.line("}");
        sb.line("}");


        String javaName = getJavaTypeName(ti, false, false);

        if(ti.type== TypeInfoType.MESSAGE) {
            sb.line("public " + javaName
                    + " create" + singular(upperCaseName(field.getName()),ti.repeated) + "() {");
            sb.line("return pool.take(" + javaName + ".class);");
            sb.line("}");
        }

        if(isMutableOrList(ti)){


            if(ti.repeated) {

                if(ti.type!= TypeInfoType.MESSAGE) {

                    String argJavaName = getJavaTypeName(ti, true, false);

                    sb.line("public " + thisClass
                            + " add" + singular(upperCaseName(field.getName()),true) + "("+argJavaName+" val) {");
                    //String javaTypeName="StringBuilder";

                    sb.line("if(this." + field.getName() + "==null) {");
                    sb.line("this." + field.getName() + "="+ makeTakeList(ti)+";");
                    sb.line("}");
                    sb.line("this." +info.fieldSetVar + "|=" + info.bits.get(field.getName()) + ";");
                    if(isMutable(ti)) {
                        sb.line(javaName + " sb = pool.take(" + javaName + ".class);");
                        sb.line("sb.append(val);");
                        sb.line("this." + field.getName() + ".add(sb);");
                    } else {
                        sb.line("this." + field.getName() + ".add(val);");
                    }
                    sb.line("return this;");
                    sb.line("}");
                    String argJavaNameList = getJavaTypeName(ti, true, true);


                    sb.line("public " + thisClass
                            + " add" + upperCaseName(field.getName()) + "("+argJavaNameList+" vals) {");
                    sb.line("this." +info.fieldSetVar + "|=" + info.bits.get(field.getName()) + ";");

                    sb.line("if(this." + field.getName() + "==null) {");
                    sb.line("this." + field.getName() + "="+ makeTakeList(ti)+";");
                    sb.line("}");
                    sb.line("for(int n=0,size=vals.size();n<size;n++){");

                    if(isMutable(ti)) {

                        sb.line(javaName + " sb = pool.take(" + javaName + ".class);");
                        sb.line("sb.append(vals.get(n));");
                        sb.line("this." + field.getName() + ".add(sb);");
                    } else {
                        sb.line("this." + field.getName() + ".add(vals.get(n));");
                    }
                    sb.line("}");

                    sb.line("return this;");
                    sb.line("}");

                } else {
                    sb.line("public " + thisClass
                            + " add" + singular(upperCaseName(field.getName()),ti.repeated) + "("+javaName+" val) {");

                    sb.line("this." +info.fieldSetVar + "|=" + info.bits.get(field.getName()) + ";");
                    sb.line("if (null==" + field.getName() + ") {");
                    sb.line(field.getName() + "="+ makeTakeList(ti)+";");
                    sb.line("}");
                    sb.line( field.getName() + ".add(val);");
                    sb.line("return this;");
                    sb.line("}");

                    sb.line("public " + javaName
                            + " add" + upperCaseName(field.getName()) + "Elem() {");
                    createAddMethod(sb, info, ti, field);
                    sb.line("}");


                }

                sb.line("public int get" + upperCaseName(field.getName()) + "Size() {");
                sb.line("return " + field.getName() + ".size();");
                sb.line("}");
            } else {

                sb.line("public " + javaName
                        + " init" + upperCaseName(field.getName()) + "() {");
                createAddMethod(sb, info, ti, field);
                sb.line("}");

                if(ti.type== TypeInfoType.STRING) {
                    sb.line("public " + thisClass + " set" + upperCaseName(field.getName()) +
                            "(" + getJavaTypeName(ti, true) + " val) {");
                    addSetValue(sb, ti, field, "val", info , "this.");
                    sb.line("return this;");
                    sb.line("}");
                } else {
                    sb.line("public " + thisClass + " set" + upperCaseName(field.getName()) +
                            "(" + getJavaTypeName(ti, true) + " val) {");
                    addSetValue(sb, ti, field, "val", info, "this.");
                    sb.line("return this;");
                    sb.line("}");

                }

            }

        } else {
            sb.line("public "+thisClass+" set"+ upperCaseName(field.getName())+
                    "("+ getJavaTypeName(ti, true) + " val) {");
            addSetValue(sb, ti, field, "val",info, "this.");
            sb.line("return this;");
            sb.line("}");

        }
    }

    private void generateWrite(JavaOutput sb, DescriptorProtos.DescriptorProto pp) {
        sb.line("public void write(CodedOutputStream os, "+ FastProtoWriter.class.getName()+" writer) throws java.io.IOException {");

        for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()){

            TypeInfo ti = getJavaTypeInfo(field);
            sb.line("if((fieldsSet & FieldBit."+field.getName()+")!=0) {");
            if(ti.repeated){

                sb.line("for(int n=0,size="+field.getName()+".size();n<size;n++){");
                if(field.getType()== DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING) {
                    sb.line("writer.writeString(FieldNum." + field.getName() + ",os,this." + field.getName() + ".get(n));");
                } else {
                    sb.line("writer.writeMessage(FieldNum." + field.getName() + ",os,this." + field.getName() + ".get(n));");
                }
                sb.line("}");
            } else {
                switch(field.getType()) {
                    case TYPE_MESSAGE:
                        sb.line("writer.writeMessage(FieldNum."+field.getName()+",os,this."+field.getName()+");");
                        break;
                    case TYPE_BOOL:
                        sb.line("os.writeBool(FieldNum."+field.getName()+"," + field.getName() + ");");
                        break;
                    case TYPE_INT32:
                        sb.line("os.writeInt32(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;
                    case TYPE_INT64:
                        sb.line("os.writeInt64(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;
                    case TYPE_SINT32:
                        sb.line("os.writeSInt32(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;
                    case TYPE_SINT64:
                        sb.line("os.writeSInt64(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;

                    case TYPE_UINT32:
                        sb.line("os.writeUInt32(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;
                    case TYPE_UINT64:
                        sb.line("os.writeUInt64(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;
                    case TYPE_FIXED32:
                        sb.line("os.writeFixed32(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;
                    case TYPE_FIXED64:
                        sb.line("os.writeFixed64(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;

                    case TYPE_SFIXED32:
                        sb.line("os.writeSFixed32(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;
                    case TYPE_SFIXED64:
                        sb.line("os.writeSFixed64(FieldNum." +field.getName()+"," +field.getName() + ");");
                        break;

                    case TYPE_DOUBLE:
                        sb.line("os.writeDouble(FieldNum." +field.getName()+"," + field.getName() + ");");
                        break;
                    case TYPE_FLOAT:
                        sb.line("os.writeFloat(FieldNum." +field.getName()+"," + field.getName() + ");");
                        break;
                    case TYPE_STRING:
                        sb.line("writer.writeString(FieldNum." +field.getName()+",os," + field.getName() + ");");
                        break;

                    default:
                        sb.line("throw new UnsupportedOperationException();");
                        break;

                }

            }
            sb.line("}");

        }
        sb.line("}");
    }

    private void generateClear(JavaOutput sb, DescriptorProtos.DescriptorProto pp, ClassInfo info) {
        sb.line("@Override");
        sb.line("public void clear(){");
        sb.line("fieldsSet=0;");

        for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()) {
            TypeInfo ti = getJavaTypeInfo(field);
            if (ti.repeated ||
                    field.getType()== DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING ||
                    field.getType()== DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE
            ) {
                sb.line("if(this."+ field.getName()+"!=null){");
                sb.line("this.pool.returnSpecific(this."+ field.getName()+");");
                sb.line("this."+ field.getName()+"=null;");
                sb.line("}");
            }
        }
        for(OneOf e:info.oneOfs.values()){
            sb.line("this."+e.valueName+"=null;");
        }
        sb.line("}");
    }

    private void generateToString(JavaOutput sb, DescriptorProtos.DescriptorProto pp) {
        sb.line("@Override");
        sb.line("public String toString(){");
        sb.line("StringBuilder sb = new StringBuilder();");
        for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()) {
            sb.line("if((fieldsSet & FieldBit."+field.getName()+")!=0) {");
            sb.line("if(sb.length()>0) sb.append(\";\");");
            sb.line("sb.append(\""+field.getName()+"=\").append("+field.getName()+");");
            sb.line("}");
        }
        sb.line("return sb.toString();");
        sb.line("}");
    }

    private ClassInfo calcClassInfo(DescriptorProtos.DescriptorProto pp) {
        ClassInfo info = new ClassInfo();

        int fieldNum = 0;
        for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()) {
            int bit = 1<<fieldNum++;
            info.bits.put(field.getName(),bit);
            if(field.hasOneofIndex()){
                OneOf oo = info.oneOfs.computeIfAbsent(field.getOneofIndex(), OneOf::new);
                oo.fields.add(field.getName());
                oo.flags|=bit;
            }

            FieldInfo fi = new FieldInfo(field.getName());
            info.fields.add(fi);
        }

        info.className=pp.getName();
        return info;
    }

    private String testOutput;
    private String javaPackage;
    private String javaClassName;

    /**
     * <p>Getter for the field <code>testOutput</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTestOutput(){
        return testOutput;
    }

    /**
     * <p>getMainClassName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMainClassName(){
        return javaClassName;
    }


    /**
     * <p>getMainPackageName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMainPackageName(){
        return javaPackage;
    }
    /**
     * <p>addParseMethodHelpers.</p>
     *
     * @param sb a {@link org.ebfhub.fastprotobuf.JavaOutput} object.
     * @param pp a {@link com.google.protobuf.DescriptorProtos.DescriptorProto} object.
     * @param info a {@link org.ebfhub.fastprotobuf.FastProtoGenerator.ClassInfo} object.
     */
    public void addParseMethodHelpers(JavaOutput sb, DescriptorProtos.DescriptorProto pp, ClassInfo info) {
        Map<TypeInfo, List<DescriptorProtos.FieldDescriptorProto>> byType=new HashMap<>();

        for(DescriptorProtos.FieldDescriptorProto.Type t:new DescriptorProtos.FieldDescriptorProto.Type[]{
                DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL,
                DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT,
                DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE,
                DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32,
                DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64,
                DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING,
        }) {
            byType.put(new TypeInfo(null, t, false), new ArrayList<>());
        }
        for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()) {
            TypeInfo info1 = getJavaTypeInfo(field);
            byType.computeIfAbsent(info1, a -> new ArrayList<>()).add(field);
        }

        sb.line("@Override");
        sb.line("public "+FastProtoSetter.class.getName()+" getSetter() { return _setter; }");

        sb.line("private final " +FastProtoSetter.class.getName()+" _setter = new "+FastProtoSetter.class.getName()+"(){");
        createAdd(sb, info, byType, info.className+".this.", false, true);
        createSet(sb, info, byType, info.className+".this.", false, true);

        sb.line("@Override");
        sb.line("public "+FastProtoField.class.getName()+" field_getDef(int fieldNum){");
        sb.line("switch(fieldNum){");
        for(DescriptorProtos.FieldDescriptorProto field:pp.getFieldList()){
            sb.line("case FieldNum."+field.getName()+": return Field."+field.getName()+";");
        }
        sb.line("default: throw new UnsupportedOperationException();");
        sb.line("}");
        sb.line("}");
        sb.blank();

        sb.line("private final java.util.List<"+FastProtoField.class.getName()+"> field_all = java.util.Arrays.asList("+
                pp.getFieldList().stream().map(a->"Field."+a.getName()).collect(Collectors.joining(", "))+");");
        sb.blank();
        sb.line("@Override");
        sb.line("public java.util.List<"+FastProtoField.class.getName()+"> field_getAll(){");
        sb.line("return field_all;");
        sb.line("}");
        sb.blank();
        sb.line("@Override");
        sb.line("public void clear(){");
        sb.line(info.className+".this.clear();");
        sb.line("}");

        sb.line("@Override");
        sb.line("public String toString(){");
        sb.line("return "+info.className+".this.toString();");
        sb.line("}");

        sb.blank();
        sb.line("};");
        sb.blank();
        sb.line("// Private impls");

        createAdd(sb, info, byType, "this.", true, false);
        createSet(sb, info, byType, "this.", true, false);

        sb.blank();

    }

    /**
     * <p>createSet.</p>
     *
     * @param sb a {@link org.ebfhub.fastprotobuf.JavaOutput} object.
     * @param info a {@link org.ebfhub.fastprotobuf.FastProtoGenerator.ClassInfo} object.
     * @param byType a {@link java.util.Map} object.
     * @param thisClass What to use for link
     * @param makePrivate Make the fn private
     * @param defer Defer to main class
     */
    public void createSet(JavaOutput sb, ClassInfo info, Map<TypeInfo, List<DescriptorProtos.FieldDescriptorProto>> byType, String thisClass, boolean makePrivate, boolean defer) {

        for(Map.Entry<TypeInfo, List<DescriptorProtos.FieldDescriptorProto>> b : byType.entrySet()){

            List<DescriptorProtos.FieldDescriptorProto> fields = b.getValue();
            TypeInfo type = b.getKey();
            String javaType = getJavaTypeName(b.getKey(),true);


            if(type.type != TypeInfoType.STRING&&!type.repeated){

                if(type.typeName==null) {
                    if(!makePrivate)
                        sb.line("@Override");
                }
                sb.line((makePrivate?"private ":"public ")+"void field_set(int field, " + javaType + " val) {");
                if(defer) {
                    sb.line(thisClass+"field_set(field,val);");
                } else {

                    sb.line("    switch(field) {");

                    for (DescriptorProtos.FieldDescriptorProto field : fields) {
                        sb.line("case FieldNum." + field.getName() + ":");
                        addSetValue(sb, type, field, "val", info, thisClass);

                        sb.line("break;");
                    }

                    sb.line("default: throw new UnsupportedOperationException(\"Unable to set field \"+field+\" from " + javaType + "\");");


                    sb.line("}");

                }
                sb.line("}");
            }
        }

        makeStringBuilderGetter(sb, info,byType, thisClass, makePrivate, defer,false);
        makeStringBuilderGetter(sb, info,byType, thisClass, makePrivate, defer,true);
    }

    private void makeStringBuilderGetter(JavaOutput sb, ClassInfo info, Map<TypeInfo, List<DescriptorProtos.FieldDescriptorProto>> byType,
                                         String thisClass,boolean makePrivate, boolean defer, boolean repeat) {

        String name = repeat?"field_add_builder":"field_builder";
        if(makePrivate)
        {
            sb.line("private StringBuilder "+name+"(int field) {");

        }
        else {
            sb.line("@Override");
            sb.line("public StringBuilder "+name+"(int field) {");
            if(defer){
                sb.line("return "+thisClass+name+"(field);");
                sb.line("}");
                return;
            }
        }
        sb.line("switch(field) {");

        for(Map.Entry<TypeInfo, List<DescriptorProtos.FieldDescriptorProto>> b : byType.entrySet()) {

            List<DescriptorProtos.FieldDescriptorProto> fields = b.getValue();
            TypeInfo type = b.getKey();
            String javaType = getJavaTypeName(b.getKey(), true);


            if (type.type == TypeInfoType.STRING) {
                for (DescriptorProtos.FieldDescriptorProto field : fields) {

                    if(repeat != type.repeated){
                        continue;
                    }

                    sb.line("case FieldNum." + field.getName() + ":");
                    if (field.hasOneofIndex()) {
                        sb.line(thisClass + info.fieldSetVar + "=" + thisClass + info.fieldSetVar + "& ~(" +
                                info.oneOfs.get(field.getOneofIndex()).fields.stream().map(a -> "FieldBit." + a).collect(Collectors.joining("|")) +
                                ")|FieldBit." + field.getName() + ";");

                    } else {
                        sb.line(thisClass + info.fieldSetVar + "|=" + info.bits.get(field.getName()) + ";");
                    }
                    genOneOfSet(sb, info, field);


                    if (type.repeated) {
                        sb.line("if(this." + field.getName() + "==null) {");
                        sb.line("this." + field.getName() + "=" + makeTakeList(type) + ";");
                        sb.line("}");
                        sb.line("this." + info.fieldSetVar + "|=" + info.bits.get(field.getName()) + ";");
                        sb.line("StringBuilder sb = pool.take(StringBuilder.class);");
                        sb.line("this." + field.getName() + ".add(sb);");


                        sb.line("return sb;");


                    } else {
                        sb.line("if(" + thisClass + field.getName() + "==null) {");
                        sb.line(thisClass + field.getName() + " = pool.take(StringBuilder.class);");
                        sb.line("}");


                        sb.line("return " + thisClass + field.getName() + ";");
                    }
                }
            }
        }
        sb.line("default: throw new UnsupportedOperationException(\"Unable to get string builder field \"+field);");
        sb.line("}");
        sb.line("}");
    }

    /**
     * <p>createAdd.</p>
     *
     * @param sb a {@link org.ebfhub.fastprotobuf.JavaOutput} object.
     * @param info a {@link org.ebfhub.fastprotobuf.FastProtoGenerator.ClassInfo} object.
     * @param byType a {@link java.util.Map} object.
     * @param thisClass What to use for link
     * @param makePrivate Make the fn private
     * @param defer Defer to main class
     */
    public void createAdd(JavaOutput sb, ClassInfo info, Map<TypeInfo, List<DescriptorProtos.FieldDescriptorProto>> byType, String thisClass, boolean makePrivate, boolean defer) {

        if(!makePrivate){
            sb.line("@Override");
        }
        sb.line((makePrivate?"private ":"public ") + FastProtoMessage.class.getName() + " field_add(int field) {");
         if(defer){
            sb.line("return "+thisClass+"field_add(field);");
        } else {
            sb.line("    switch(field) {");
            for (Map.Entry<TypeInfo, List<DescriptorProtos.FieldDescriptorProto>> b : byType.entrySet()) {

                List<DescriptorProtos.FieldDescriptorProto> fields = b.getValue();
                TypeInfo type = b.getKey();

                if (type.type == TypeInfoType.MESSAGE) {
                    for (DescriptorProtos.FieldDescriptorProto field : fields) {
                        sb.line("case FieldNum." + field.getName() + ":");

                        createAddMethod(sb, info, type, field);
                    }

                }
            }
            sb.line("default: throw new UnsupportedOperationException(\"Unable to add\");");


            sb.line("}");
        }
        sb.line("}");
    }

    String makeTakeList(TypeInfo type){
        if(type.type== TypeInfoType.STRING||
        type.type== TypeInfoType.MESSAGE) {
            return "pool.takeList()";
        } else {
            String javaTypeName = getJavaTypeName(type, false, false);
            return "pool.take"+upperCaseName(javaTypeName)+"List()";
        }

    }

    /**
     * <p>createAddMethod.</p>
     *
     * @param sb a {@link org.ebfhub.fastprotobuf.JavaOutput} object.
     * @param info a {@link org.ebfhub.fastprotobuf.FastProtoGenerator.ClassInfo} object.
     * @param type a {@link org.ebfhub.fastprotobuf.FastProtoGenerator.TypeInfo} object.
     * @param field a {@link com.google.protobuf.DescriptorProtos.FieldDescriptorProto} object.
     */
    public void createAddMethod(JavaOutput sb, ClassInfo info, TypeInfo type, DescriptorProtos.FieldDescriptorProto field) {
        String javaTypeName = getJavaTypeName(type, false, false);
        genOneOfSet(sb, info, field);


        if(type.repeated) {
            sb.line("if (null==" + field.getName() + ") {");
            sb.line(field.getName() + "="+ makeTakeList(type)+";");
            sb.line("}");
            sb.line(info.fieldSetVar + "|=" + info.bits.get(field.getName()) + ";");
            sb.line(javaTypeName + " "+field.getName()+"_res = pool.take(" + javaTypeName + ".class);");
            sb.line( field.getName() + ".add("+field.getName()+"_res);");
            sb.line("return "+field.getName()+"_res;");
        } else {
            sb.line("if (null==" + field.getName() + ") {");
            sb.line(field.getName() + "=pool.take("+javaTypeName+".class);");
            sb.line("}");
            sb.line(info.fieldSetVar + "|=FieldBit." + field.getName() + ";");
            sb.line("return "+field.getName()+";");
        }

    }

    private void genOneOfSet(JavaOutput sb, ClassInfo info, DescriptorProtos.FieldDescriptorProto field) {
        if (field.hasOneofIndex()) {
            OneOf ii = info.oneOfs.get(field.getOneofIndex());
            sb.line(ii.valueName + "=" + ii.className + "." + field.getName() + ";");
        }
    }

    private String upperCaseName(String name) {

        if (name != null && name.length() != 0) {
            char[] chars = name.replaceAll("^_","").toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        } else {
            return name;
        }
    }
    private String singular(String name, boolean apply) {

        return !apply?name:name.replaceAll("s$","");
    }
    static class OneOf
    {
        private final String className;
        private final String valueName;

        OneOf(int num){
            this.className = num==0?"OneOf":"OneOf_"+num;
            this.valueName = num==0?"oneOf":"oneOf_"+num;
        }
        List<String> fields = new ArrayList<>();
        int flags=0;
    }
    static class ClassInfo
    {
        String className;
        Map<String,Integer> bits = new HashMap<>();
        String fieldSetVar = "fieldsSet";
        Map<Integer,OneOf> oneOfs=new HashMap<>();
        List<FieldInfo> fields= new ArrayList<>();
    }

    static class FieldInfo
    {
        final String name;
        public FieldInfo(String name) {
            this.name=name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * <p>addSetValue.</p>
     *
     * @param sb a {@link org.ebfhub.fastprotobuf.JavaOutput} object.
     * @param typeInfo a {@link org.ebfhub.fastprotobuf.FastProtoGenerator.TypeInfo} object.
     * @param field a {@link com.google.protobuf.DescriptorProtos.FieldDescriptorProto} object.
     * @param paramName a {@link java.lang.String} object.
     * @param info a {@link org.ebfhub.fastprotobuf.FastProtoGenerator.ClassInfo} object.
     * @param thisStr Access the current object
     */
    public void addSetValue(JavaOutput sb, TypeInfo typeInfo,
                            DescriptorProtos.FieldDescriptorProto field, String paramName,
                            ClassInfo info, String thisStr) {
        if(isMutableOrList(typeInfo)) {
            String javaTypeName = getJavaTypeName(typeInfo, false, false);

            if(typeInfo.repeated){
                // copy whole list
                sb.line("if("+thisStr + field.getName() + "==null) {");
                sb.line(thisStr + field.getName() + "="+ makeTakeList(typeInfo)+";");
                sb.line("} else {");
                sb.line("pool.clearList(this." + field.getName() + ");");
                sb.line("}");
                sb.line("for (int n=0,size="+paramName+".size();n<size;n++){");
                sb.line(javaTypeName+" sb = pool.take("+javaTypeName+".class);");
                sb.line("sb.append(" + paramName + ".get(n));");
                sb.line(thisStr + field.getName() + ".add(sb);");
                sb.line("}");

            } else if(typeInfo.type== TypeInfoType.STRING){
                sb.line("if("+thisStr + field.getName() + "==null) {");
                sb.line(thisStr + field.getName() + "=pool.take("+javaTypeName+".class);");
                sb.line("}");
                sb.line(thisStr + field.getName() + ".setLength(0);");
                sb.line(thisStr + field.getName() + ".append(" + paramName + ");");
            } else {
                sb.line("if("+thisStr+field.getName() + "!=null){");
                sb.line("pool.returnSpecific(" +thisStr+ field.getName() + ");");
                sb.line("}");


                sb.line(thisStr+field.getName() + "="+paramName+";");

            }
        } else {
            sb.line(thisStr+field.getName() + "="+paramName+";");

        }
        sb.line(info.fieldSetVar+"|="+info.bits.get(field.getName())+";");

        genOneOfSet(sb, info, field);
    }

    private boolean isMutableOrList(TypeInfo typeInfo) {
        return typeInfo.type== TypeInfoType.MESSAGE||typeInfo.type== TypeInfoType.STRING||typeInfo.repeated;
    }
    private boolean isMutable(TypeInfo typeInfo) {
        return typeInfo.type== TypeInfoType.MESSAGE||
                typeInfo.type== TypeInfoType.STRING;
    }

    private String extractPackageName(DescriptorProtos.FileDescriptorProto proto) {
        DescriptorProtos.FileOptions options = proto.getOptions();
        if (options != null) {
            String javaPackage = options.getJavaPackage();
            if (!Strings.isNullOrEmpty(javaPackage)) {
                return javaPackage;
            }
        }

        return Strings.nullToEmpty(proto.getPackage());
    }

    private static Map<DescriptorProtos.FieldDescriptorProto.Type,String> javaTypes(){
        Map<DescriptorProtos.FieldDescriptorProto.Type,String> p = new HashMap<>();
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL, "boolean");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE, "double");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32, "int");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64, "long");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32, "int");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64, "long");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT32, "int");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT64, "long");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED32, "int");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED64, "long");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED32, "int");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED64, "long");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING, "StringBuilder");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE, "Object");
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT, "float");
        return p;

    }
    private Map<DescriptorProtos.FieldDescriptorProto.Type,WireFormat.FieldType> javaTypes1(){
        Map<DescriptorProtos.FieldDescriptorProto.Type,WireFormat.FieldType> p = new HashMap<>();
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL, WireFormat.FieldType.BOOL);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE, WireFormat.FieldType.DOUBLE);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT, WireFormat.FieldType.FLOAT);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32, WireFormat.FieldType.INT32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64, WireFormat.FieldType.INT64);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING, WireFormat.FieldType.STRING);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE,WireFormat.FieldType.MESSAGE);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32, WireFormat.FieldType.SINT32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64, WireFormat.FieldType.SINT64);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT32, WireFormat.FieldType.UINT32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT64, WireFormat.FieldType.UINT64);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED32, WireFormat.FieldType.SFIXED32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED64, WireFormat.FieldType.SFIXED64);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED32, WireFormat.FieldType.FIXED32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED64, WireFormat.FieldType.FIXED64);
        return p;

    }
    private Map<DescriptorProtos.FieldDescriptorProto.Type,String> typeToJava = javaTypes();
    private Map<DescriptorProtos.FieldDescriptorProto.Type,WireFormat.FieldType> wireTypes = javaTypes1();

    private static Map<DescriptorProtos.FieldDescriptorProto.Type,TypeInfoType> typeInfoToType(){
        Map<DescriptorProtos.FieldDescriptorProto.Type,TypeInfoType> p = new HashMap<>();
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL, TypeInfoType.BOOL);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE, TypeInfoType.DOUBLE);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT, TypeInfoType.FLOAT);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32, TypeInfoType.INT32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64, TypeInfoType.INT64);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING, TypeInfoType.STRING);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE,TypeInfoType.MESSAGE);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32, TypeInfoType.INT32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64, TypeInfoType.INT64);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT32, TypeInfoType.INT32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT64, TypeInfoType.INT64);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED32, TypeInfoType.INT32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED64, TypeInfoType.INT64);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED32, TypeInfoType.INT32);
        p.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED64, TypeInfoType.INT64);
        return p;

    }

    enum TypeInfoType
    {
        BOOL,
        STRING,
        MESSAGE,
        FLOAT,
        DOUBLE,
        INT32,
        INT64
    }

    static class TypeInfo
    {
        public TypeInfo(String typeName, DescriptorProtos.FieldDescriptorProto.Type type,boolean repeated) {
            this.typeName = typeName;
            this.repeated = repeated;
            this.type = typeInfoToType().get(type);
            this.javaClass = javaTypes().get(type);
        }

        String typeName;
        boolean repeated;
        TypeInfoType type;
        String javaClass;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TypeInfo typeInfo = (TypeInfo) o;
            return repeated == typeInfo.repeated &&
                    Objects.equals(typeName, typeInfo.typeName) &&
                    type == typeInfo.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeName, repeated, type);
        }
    }

    private TypeInfo getJavaTypeInfo(DescriptorProtos.FieldDescriptorProto field) {
        DescriptorProtos.FieldDescriptorProto.Type type = field.getType();

        return new TypeInfo(field.hasTypeName() ? pNameToName.get(field.getTypeName()) : null, type, field.getLabel() == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED);
    }

    private String getJavaTypeName(TypeInfo info, boolean isInput){
        return getJavaTypeName(info,isInput,info.repeated);
    }

    private String getJavaTypeName(TypeInfo info, boolean isInput, boolean asLisyt){

        String name;
        if (info.typeName!=null) {
            name =info.typeName;

        } else {
            if(isInput&&info.type==TypeInfoType.STRING){
                name= "CharSequence";
            } else{
                name=info.javaClass;
            }
        }

        if(asLisyt){
            if(name.equals("int")){
                return "gnu.trove.list.array.TIntArrayList";
            }

            if(isInput) {
                if(name.equals("CharSequence")){
                    return "java.util.List<? extends " + name + ">";

                }

                return "java.util.List<" + name + ">";
            } else {
                return "java.util.ArrayList<" + name + ">";

            }
        } else {
            return name ;
        }
    }

    private String absoluteFileName(String packageName, String fileName) {
        String dir = packageName.replace('.', '/');
        if (Strings.isNullOrEmpty(dir)) {
            return fileName;
        } else {
            return dir + "/" + fileName;
        }
    }

    private PluginProtos.CodeGeneratorResponse.File buildFile(String packageName, String fileName, String content) {
        return makeFile(absoluteFileName(packageName,fileName), content);
    }
//
//    private String getComments(DescriptorProtos.SourceCodeInfo.Location location) {
//        return location.getLeadingComments().isEmpty() ? location.getTrailingComments() : location.getLeadingComments();
//    }
//
//    private String getJavaDoc(String comments, String prefix) {
//        if (!comments.isEmpty()) {
//            StringBuilder builder = new StringBuilder("/**\n")
//                    .append(prefix).append(" * <pre>\n");
//            Arrays.stream(HtmlEscapers.htmlEscaper().escape(comments).split("\n"))
//                    .forEach(line -> builder.append(prefix).append(" * ").append(line).append("\n"));
//            builder
//                    .append(prefix).append(" * <pre>\n")
//                    .append(prefix).append(" */");
//            return builder.toString();
//        }
//        return null;
//    }
}
