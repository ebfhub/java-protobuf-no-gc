package com.github.ebfhub.fastprotobuf;

import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessage;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessageFast;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.ebfhub.fastprotobuf.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TestEncoding {


    static byte[] decode(String line){
        return  Base64.getDecoder().decode(line);
    }

    String good="AE8QAlIJCAAo7M7+/J5cUgkIAijszv78nlxSCQgEKOzO/vyeXFIJCAYo7M7+/J5cUgQICCAAUgUICiDEAVIFCAwgxgFSCQgOKOzO/vyeXA==";
    String bad="AE8QBFIJCAAo/Jz//J5cUgkIAij8nP/8nlxSCQgEKPyc//yeXFIJCAYo/Jz//J5cUgQICCAAUgUICiDGAVIFCAwgyAFSCQgOKPyc//yeXA==";

    @Test
    public void testBadMessage() throws IOException {
        byte[] bb = decode(bad);
        CodedInputStream is = CodedInputStream.newInstance(bb, 2, bb.length - 2);
        FastProtoReader reader=new FastProtoReader();
        FastProtoObjectPool pool=new FastProtoObjectPool();
        SampleMessageFast.DataMessage msg =  SampleMessageFast.DataMessage.create(pool);
        reader.parse(is,msg.getSetter());

        System.out.println(msg);

    }

    @Test
    public void testGoodMessage() throws IOException {
        byte[] bb = decode(good);
        CodedInputStream is = CodedInputStream.newInstance(bb, 2, bb.length - 2);
        FastProtoReader reader=new FastProtoReader();
        FastProtoObjectPool pool=new FastProtoObjectPool();
        SampleMessageFast.DataMessage msg =  SampleMessageFast.DataMessage.create(pool);
        reader.parse(is,msg.getSetter());

        System.out.println(msg);

    }
    @Test
    public void encodingTest() throws IOException {
        SampleMessage.DataMessage msg = SampleMessage.DataMessage.newBuilder()
                .setSymbol("sym1")
                .setSymbolId(12)
                .setSentTs(System.currentTimeMillis())
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(123).setBool(true))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(126).setString("hello"))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(190).setFloat(98.97f))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(126).setDouble(98.99)
                        .build())
                .build();


        byte[] bytes=msg.toByteArray();
        System.out.println("Bytes = "+new String(bytes));
        System.out.println(ProtoDebug.decodeProto(bytes,false));


        CodedInputStream is=CodedInputStream.newInstance(bytes);


        FastProtoReader reader = new FastProtoReader();
        FastProtoObjectPool pool = reader.getPool();
        FastProtoWriter writer=new FastProtoWriter();


        SampleMessageFast.DataMessage msg1 = SampleMessageFast.DataMessage.create(pool);
        reader.parse(is,msg1.getSetter());


        ReusableByteArrayOutputStream os1 = new ReusableByteArrayOutputStream();
        CodedOutputStream o2 =  CodedOutputStream.newInstance(os1);
        msg1.write(o2,writer);
        o2.flush();


        byte[] bytes2=os1.toByteArray();

        byte[] bytes3=null;
        SampleMessageFast.DataMessage msg2 =  SampleMessageFast.DataMessage.create(pool);
        SampleMessageFast.DataMessage msg3 =  SampleMessageFast.DataMessage.create(pool);
        msg2.setSymbol("bye");

        for(int k=0;k<10;k++){
            msg2.addValue(msg2.createValue()
                    .setString("fifty"+k).setBool(true).setFieldId(k));
        }

        MutableByteArrayInputStream mis = new MutableByteArrayInputStream();
        CodedInputStream is3=CodedInputStream.newInstance(mis);

        List<String> s = Arrays.asList("one","two");

        for(int n=0;n<4;n++) {
            msg2.clear();
            msg2
                    .setSymbol("sym12")
                    .setSentTs(System.currentTimeMillis())
                    .setSymbolId(123)
                    .setDefineFieldSet(msg2.createDefineFieldSet().setFieldSetId(1000))

                    .addValue(
                            msg2.createValue()
                                .setString("sym14")
                                .setFieldId(1000)
                                .setStringList(SampleMessageFast.StringList.create(pool).addStrings(s)
                                        .addString("onwe")
                                        .addString("strTwo")
                                        .addString("ninety")
                                        .addString("str3"))

                    );

            os1.reset();
            msg1.write(o2,writer);
            o2.flush();


            byte[] tmp=os1.toByteArray();

            if (n > 0) {
                assertArrayEquals("eq=" + new String(tmp), tmp, bytes3);
            }
            bytes3 = tmp;

            byte[] tmp1=os1.getBytes();
            int tmpLen = os1.size();
            msg3.clear();
            reader.readItem(msg3,tmp1,0,tmpLen);
        }
    }

    @Test
    public void testStringList() throws IOException {
        FastProtoReader reader = new FastProtoReader();
        FastProtoObjectPool pool = reader.getPool();
        FastProtoWriter writer=new FastProtoWriter();


        SampleMessageFast.DataMessage msg2 =  SampleMessageFast.DataMessage.create(pool);
        SampleMessageFast.DataMessage msg3 =  SampleMessageFast.DataMessage.create(pool);

        List<String> s = Arrays.asList("one","two");

        msg2
                .setSymbol("sym12")
                .addValue(
                        msg2.createValue()
                                .setString("sym14")
                                .setFieldId(1000)
                                .setStringList(SampleMessageFast.StringList.create(pool).addStrings(s)
                                        .addString("a")
                                        .addString("b")
                                        .addString("c")
                                        .addString("d1000"))

                );

        SampleMessageFast.StringList sl = msg2.getValues().get(0).getStringList();
        assertEquals(Arrays.asList("one","two","a","b","c","d1000"), sl.getStrings().stream().map(a->a.toString()).collect(Collectors.toList()));

        ReusableByteArrayOutputStream os1 = new ReusableByteArrayOutputStream();
        CodedOutputStream o2 =  CodedOutputStream.newInstance(os1);
        os1.reset();
        msg2.write(o2,writer);
        o2.flush();


        byte[] tmp1=os1.getBytes();
        int tmpLen = os1.size();
        msg3.clear();
        reader.readItem(msg3,tmp1,0,tmpLen);

        assertEquals("sym12", msg3.getSymbol().toString());

        List<SampleMessageFast.FieldAndValue> vals = msg3.getValues();
        assertEquals(1,vals.size());
        SampleMessageFast.StringList list = vals.get(0).getStringList();
        assertEquals(Arrays.asList("one","two","a","b","c","d1000"), list.getStrings().stream().map(a->a.toString()).collect(Collectors.toList()));
    }


    @Test
    public void testSimple() throws IOException {
        FastProtoReader reader = new FastProtoReader();
        FastProtoObjectPool pool = reader.getPool();
        FastProtoWriter writer = new FastProtoWriter();
        SampleMessageFast.DataMessage msg2 = SampleMessageFast.DataMessage.create(pool);
        SampleMessageFast.DataMessage msg3 = SampleMessageFast.DataMessage.create(pool);
        msg2
                .setSymbol("sym12")
                .addValue(
                        msg2.createValue()
                                .setFieldId(1000)
                                .setDouble(99.5))

        ;
        assertEquals(1000,msg2.getValues().get(0).getFieldId());

        ReusableByteArrayOutputStream os1 = new ReusableByteArrayOutputStream();
        CodedOutputStream o2 = CodedOutputStream.newInstance(os1);
        os1.reset();
        msg2.write(o2, writer);
        o2.flush();


        byte[] tmp1 = os1.getBytes();
        int tmpLen = os1.size();
        msg3.clear();
        reader.readItem(msg3, tmp1, 0, tmpLen);

        assertEquals(1000,msg3.getValues().get(0).getFieldId());


    }
        @Test
    public void testRandomData() throws IOException {
            FastProtoReader reader = new FastProtoReader();
            FastProtoObjectPool pool = reader.getPool();
            FastProtoWriter writer = new FastProtoWriter();

        for(int n=0;n<1000;n++) {


            SampleMessageFast.DataMessage msg2 = SampleMessageFast.DataMessage.create(pool);
            SampleMessageFast.DataMessage msg3 = SampleMessageFast.DataMessage.create(pool);

            List<String> s = Arrays.asList("one", "two");

            msg2
                    .setSymbol("sym12")
                    .addValue(
                            msg2.createValue()
                                    .setFieldId(1000)
                                    .setStringList(SampleMessageFast.StringList.create(pool).addStrings(s)
                                            .addString("a")
                                            .addString("b")
                                            .addString("c")
                                            .addString("d1000"))

                    )
                    .addValue(
                            msg2.createValue()
                                    .setFieldId(1003)
                                    .setDouble(Math.random()*n)

                    )
                    .addValue(
                            msg2.createValue()
                                    .setFieldId(1003)
                                    .setBool(true)

                    )
            ;

            SampleMessageFast.StringList sl = msg2.getValues().get(0).getStringList();
            assertEquals(Arrays.asList("one", "two", "a", "b", "c", "d1000"), sl.getStrings().stream().map(a -> a.toString()).collect(Collectors.toList()));

            ReusableByteArrayOutputStream os1 = new ReusableByteArrayOutputStream();
            CodedOutputStream o2 = CodedOutputStream.newInstance(os1);
            os1.reset();
            msg2.write(o2, writer);
            o2.flush();


            byte[] tmp1 = os1.getBytes();
            int tmpLen = os1.size();
            msg3.clear();
            reader.readItem(msg3, tmp1, 0, tmpLen);

            assertEquals("sym12", msg3.getSymbol().toString());

            List<SampleMessageFast.FieldAndValue> vals = msg3.getValues();
            assertTrue( vals.size()>=1);
            SampleMessageFast.StringList list = vals.get(0).getStringList();
            assertEquals(Arrays.asList("one", "two", "a", "b", "c", "d1000"), list.getStrings().stream().map(a -> a.toString()).collect(Collectors.toList()));

            assertEquals(msg2.toString(),msg3.toString());
            pool.returnOne(msg2);
            pool.returnOne(msg3);


        }
    }
}
