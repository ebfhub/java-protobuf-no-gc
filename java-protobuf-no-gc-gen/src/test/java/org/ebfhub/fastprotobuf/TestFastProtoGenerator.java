package org.ebfhub.fastprotobuf;

import com.salesforce.jprotoc.ProtocPlugin;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TestFastProtoGenerator {
    @Test
    public void testGenerate() throws Exception {
        FastProtoGenerator gen=new FastProtoGenerator(true);
        ProtocPlugin.debug(gen,"src/test/resources/descriptor_dump");

        String code = gen.getTestOutput();


        File dest=new File("src/test/java/com/github/ebfhub/fastprotobuf/sample/proto/SampleMessageFast.java");
        System.out.println("Writing to "+dest.getCanonicalPath());

        FileWriter w1 = new FileWriter(dest);
        w1.write(code);
        w1.close();


        String fullName=gen.getMainPackageName()+"."+gen.getMainClassName();
        InMemoryCompiler.IMCSourceCode cls1source = new InMemoryCompiler.IMCSourceCode(fullName, code);

        final List<InMemoryCompiler.IMCSourceCode> classSources = new ArrayList<>();
        classSources.add(cls1source);

        final InMemoryCompiler uCompiler = new InMemoryCompiler(classSources);
        final InMemoryCompiler.CompilerFeedback compilerFeedback = uCompiler.compile();

        assertTrue (code+": "+compilerFeedback,compilerFeedback != null && compilerFeedback.success) ;

        Class outer = uCompiler.getCompiledClass(fullName);

        Class inner = uCompiler.getCompiledClass(fullName+"$DataMessage");
        FastProtoObjectPool pool = new FastProtoObjectPool();
        FastProtoSetter o = (FastProtoSetter)inner.getMethod("create",FastProtoObjectPool.class).invoke(null,pool);

        Map<Integer,FastProtoField> found = new HashMap<>();
        Map<Integer,Object> values = new HashMap<>();

        for(FastProtoField field : o.field_getAll()){
            int num = field.num;
            found.put(num,field);
            switch(field.ft){
                case INT32:
                    o.field_set(num,101);
                    values.put(num,101);
                    break;

                case INT64:
                    o.field_set(num,102030L);
                    values.put(num,102030L);
                    break;

                case STRING:
                    o.field_builder(num).append("HELLO");
                    values.put(num,"HELLO");
                    break;
            }
        }
        assertTrue(o.toString(),o.toString().contains("HELLO"));
        assertTrue(found.size()>0);

        FastProtoWriter w = new FastProtoWriter();
    }
}
