package com.github.ebfhub.fastprotobuf;

import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessage;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessageFast;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.ebfhub.fastprotobuf.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class TestEncoding {
    @Test
    public void encodingTest() throws IOException {
        SampleMessage.Message msg = SampleMessage.Message.newBuilder()
                .setSymbol("sym1")
                .setSymbolId(12)
                .setTs(System.currentTimeMillis())
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setField(123).setBool(true))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setField(126).setString("hello"))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setField(124).setFieldName("myField").setInt32(97))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setField(190).setFloat(98.97f))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setField(126).setDouble(98.99)
                        .build())
                .build();


        byte[] bytes=msg.toByteArray();
        System.out.println("Bytes = "+new String(bytes));
        System.out.println(ProtoDebug.decodeProto(bytes,false));


        CodedInputStream is=CodedInputStream.newInstance(bytes);


        FastProtoReader reader = new FastProtoReader();
        SampleMessageFast.Message msg1 = new SampleMessageFast.Message();
        reader.parse(is,msg1);


        ReusableByteArrayOutputStream os1 = new ReusableByteArrayOutputStream();
        CodedOutputStream o2 =  CodedOutputStream.newInstance(os1);
        FastProtoWriter writer=new FastProtoWriter();
        msg1.write(o2,writer);
        o2.flush();


        byte[] bytes2=os1.toByteArray();

        byte[] bytes3=null;
        SampleMessageFast.Message msg2 = new SampleMessageFast.Message();
        SampleMessageFast.Message msg3 = new SampleMessageFast.Message();
        msg2.setSymbol("bye");

        for(int k=0;k<10;k++){
            SampleMessageFast.FieldAndValue val = msg2.addValues();
            val.set_string("fifty"+k);
            val.set_bool(true);
            val.setField(k);
            val.setFieldName("jonb"+k);
        }

        MutableByteArrayInputStream mis = new MutableByteArrayInputStream();
        CodedInputStream is3=CodedInputStream.newInstance(mis);

        for(int n=0;n<4;n++) {
            msg2.clear();

            msg2.setSymbol("sym12");
            msg2.setTs(System.currentTimeMillis());
            msg2.setSymbolId(123);
            SampleMessageFast.FieldAndValue val = msg2.addValues();
            val.set_string("sym14");
            val.setField(1000);
            val.setFieldName("sym13");

            os1.reset();
            msg1.write(o2,writer);
            o2.flush();


            byte[] tmp=os1.toByteArray();

            if (n > 0) {
                assertTrue("eq=" + new String(tmp), Arrays.equals(tmp, bytes3));
            }
            bytes3 = tmp;
            System.gc();

            byte[] tmp1=os1.getBytes();
            int tmpLen = os1.size();
            mis.setBytes(tmp1,tmpLen);
            msg3.clear();
            reader.parse(is3,msg3);
        }
    }
}
