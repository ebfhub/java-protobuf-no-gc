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
        SampleMessage.DataMessage msg = SampleMessage.DataMessage.newBuilder()
                .setSymbol("sym1")
                .setSymbolId(12)
                .setTs(System.currentTimeMillis())
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
        SampleMessageFast.DataMessage msg1 = new SampleMessageFast.DataMessage();
        reader.parse(is,msg1);


        ReusableByteArrayOutputStream os1 = new ReusableByteArrayOutputStream();
        CodedOutputStream o2 =  CodedOutputStream.newInstance(os1);
        FastProtoWriter writer=new FastProtoWriter();
        msg1.write(o2,writer);
        o2.flush();


        byte[] bytes2=os1.toByteArray();

        byte[] bytes3=null;
        SampleMessageFast.DataMessage msg2 = new SampleMessageFast.DataMessage();
        SampleMessageFast.DataMessage msg3 = new SampleMessageFast.DataMessage();
        FastProtoReader.ObjectPool pool = reader.getPool();
        msg2.setSymbol("bye",pool);

        for(int k=0;k<10;k++){
            SampleMessageFast.FieldAndValue val = msg2.addValues(pool);
            val.set_string("fifty"+k,pool);
            val.set_bool(true);
            val.setFieldId(k);
        }

        MutableByteArrayInputStream mis = new MutableByteArrayInputStream();
        CodedInputStream is3=CodedInputStream.newInstance(mis);


        for(int n=0;n<4;n++) {
            msg2.clear(pool);

            msg2.setSymbol("sym12",pool);
            msg2.setTs(System.currentTimeMillis());
            msg2.setSymbolId(123);
            SampleMessageFast.FieldAndValue val = msg2.addValues(pool);
            val.set_string("sym14",pool);
            val.setFieldId(1000);
            SampleMessageFast.StringList strList = val.init_stringList(pool);
            strList.addStrings(pool).append(12);
            strList.addStrings("strTwo",pool).addStrings("str3",pool);

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
            msg3.clear(reader.getPool());
            reader.parse(is3,msg3);
        }
    }
}
