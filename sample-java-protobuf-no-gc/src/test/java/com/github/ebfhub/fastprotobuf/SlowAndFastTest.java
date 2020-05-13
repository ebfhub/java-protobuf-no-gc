package com.github.ebfhub.fastprotobuf;

import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessage;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessageFast;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.ebfhub.fastprotobuf.*;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SlowAndFastTest {
    @Test
    public void testNewReadsOld() throws IOException {
        SampleMessage.DataMessage oldMsg = SampleMessage.DataMessage.newBuilder()
                .setMessageId(16)
                .setSentTs(1000000)
                .setSymbol("sym1")
                .setSymbolId(12)
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(123).setBool(true))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(126).setString("hello"))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(190).setFloat(98.97f))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(126).setDouble(98.99))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(126).setInt32(77))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(126).setInt64(1000000000))
                .addValues(SampleMessage.FieldAndValue.newBuilder()
                        .setFieldId(126).setNull( SampleMessage.NullValue.newBuilder()))
                .addValues(SampleMessage.FieldAndValue.newBuilder().setStringList(SampleMessage.StringList.newBuilder()
                        .addStrings("hello").addStrings("world")
                        .build()))
                .build();

        byte[] bytes=oldMsg.toByteArray();

        CodedInputStream is=CodedInputStream.newInstance(bytes);

        FastProtoReader reader = new FastProtoReader();
        FastProtoObjectPool pool = reader.getPool();
        FastProtoWriter writer=new FastProtoWriter();


        SampleMessageFast.DataMessage newMsg = SampleMessageFast.DataMessage.create(pool);
        reader.parse(is,newMsg.getSetter());


        ReusableByteArrayOutputStream os1 = new ReusableByteArrayOutputStream();
        CodedOutputStream o2 =  CodedOutputStream.newInstance(os1);
        newMsg.write(o2,writer);
        o2.flush();


        assertEquals( oldMsg.getMessageId(), newMsg.getMessageId());
        assertEquals( oldMsg.getSentTs(), newMsg.getSentTs());
        assertEquals( oldMsg.getSymbol(), newMsg.getSymbol().toString());

        assertEquals(oldMsg.getValuesList().size(), newMsg.getValues().size());
        for(int n=0;n<newMsg.getValues().size();n++) {
            assertEquals(getSList(oldMsg.getValuesList().get(n).getStringList()), getSList(newMsg.getValues().get(n).getStringList()));

            assertEquals(oldMsg.getValuesList().get(n).getBool(), newMsg.getValues().get(n).getBool());
            assertEquals(oldMsg.getValuesList().get(n).getString(), asString(newMsg.getValues().get(n).getString()));
            assertEquals(oldMsg.getValuesList().get(n).getFloat(), newMsg.getValues().get(n).getFloat(), 0);
            assertEquals(oldMsg.getValuesList().get(n).getDouble(), newMsg.getValues().get(n).getDouble(), 0);
            assertEquals(oldMsg.getValuesList().get(n).getInt32(), newMsg.getValues().get(n).getInt32());
            assertEquals(oldMsg.getValuesList().get(n).getInt64(), newMsg.getValues().get(n).getInt64());
            assertEquals(getIsNull(oldMsg.getValuesList().get(n).getNull()), getIsNull(newMsg.getValues().get(n).getNull()));
        }
    }

    private boolean getIsNull(SampleMessageFast.NullValue aNull) {
        return aNull!=null;
    }

    private boolean getIsNull(SampleMessage.NullValue n){
        return n!=SampleMessage.NullValue.getDefaultInstance();
    }

    private List<String> getSList(SampleMessage.StringList stringList) {
        if(stringList==SampleMessage.StringList.getDefaultInstance()){
            return null;
        }
        List<String> res=new ArrayList<>();
        for(int n=0;n<stringList.getStringsCount();n++){
            res.add(stringList.getStrings(n));
        }
        return res;
    }

    private List<String>  getSList(SampleMessageFast.StringList stringList) {

        if(stringList==null){
            return null;
        }
        List<String> res=new ArrayList<>();
        for(int n=0;n<stringList.getStringsSize();n++){
            res.add(stringList.getStrings().get(n).toString());
        }
        return res;

    }


    String asString(CharSequence c){
        return c==null?"":c.toString();
    }
}
