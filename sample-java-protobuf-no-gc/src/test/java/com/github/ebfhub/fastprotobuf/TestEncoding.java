package com.github.ebfhub.fastprotobuf;

import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessage;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessageFast;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.ebfhub.fastprotobuf.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
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
        FastProtoObjectPool pool = reader.getPool();
        FastProtoWriter writer=new FastProtoWriter();


        SampleMessageFast.DataMessage msg1 = SampleMessageFast.DataMessage.create(pool);
        reader.parse(is,msg1);


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
                    .set_string("fifty"+k).set_bool(true).setFieldId(k));
        }

        MutableByteArrayInputStream mis = new MutableByteArrayInputStream();
        CodedInputStream is3=CodedInputStream.newInstance(mis);


        for(int n=0;n<4;n++) {
            msg2.clear();

            msg2
                    .setSymbol("sym12")
                    .setTs(System.currentTimeMillis())
                    .setSymbolId(123)
                    .setDefineFieldSet(msg2.createDefineFieldSet().setFieldSetId(1000))

                    .addValue(
                            msg2.createValue()
                                    .set_string("sym14")
                                    .setFieldId(1000)
                                    .set_stringList(SampleMessageFast.StringList.create(pool)
                                            .addStrings("onwe")
                                            .addStrings("strTwo")
                                            .addStrings("ninety")
                                            .addStrings("str3"))

                    );

            os1.reset();
            msg1.write(o2,writer);
            o2.flush();


            byte[] tmp=os1.toByteArray();

            if (n > 0) {
                assertArrayEquals("eq=" + new String(tmp), tmp, bytes3);
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
