package com.github.ebfhub.fastprotobuf;

import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessage;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessageFast;
import com.google.protobuf.*;
import org.ebfhub.fastprotobuf.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * <p>FastProtoSampleMain class.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public class FastProtoSampleMain {
    /**v
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException if any.
     * @throws java.lang.NoSuchFieldException if any.
     * @throws java.lang.NoSuchMethodException if any.
     */
    public static void main(String[]args) throws IOException, NoSuchFieldException, NoSuchMethodException {

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
        System.out.println(ProtoDebug.decodeProto(bytes,false));

        CodedInputStream is=CodedInputStream.newInstance(bytes);

        FastProtoReader reader = new FastProtoReader();

        SampleMessageFast.DataMessage msg1 = SampleMessageFast.DataMessage.newBuilder();
        reader.parse(is,msg1.getSetter());


        ReusableByteArrayOutputStream os1 = new ReusableByteArrayOutputStream();
        CodedOutputStream o2 =  CodedOutputStream.newInstance(os1);
        FastProtoWriter writer=new FastProtoWriter();
        msg1.write(o2,writer);
        o2.flush();


        byte[] bytes2=os1.toByteArray();

        byte[] bytes3=null;
        SampleMessageFast.DataMessage msg2 =  SampleMessageFast.DataMessage.newBuilder();
        msg2.setSymbol("bye");

        for(int k=0;k<10;k++){
            SampleMessageFast.FieldAndValue val = msg2.addValuesElem();
            val.setString("fifty"+k);
            val.setBool(true);
            val.setFieldId(k);
        }

        MutableByteArrayInputStream mis = new MutableByteArrayInputStream();
        CodedInputStream is3=CodedInputStream.newInstance(mis);

        for(int n=0;n<3e10;n++) {
            msg2.clear();

            msg2.setSymbol("sym12");
            msg2.setSentTs(System.currentTimeMillis());
            msg2.setSymbolId(123);
            msg2.addValue(msg2.createValue().setString("sym14").setFieldId(1000))
                    .addFieldIdDef(msg2.createFieldIdDef().setFieldId(98).setFieldName("id1D"))
                    .addFieldIdDef(msg2.createFieldIdDef().setFieldId(99).setFieldName("anotherId"));

            os1.reset();
            msg1.write(o2,writer);
            o2.flush();

            //System.out.println("Pool size1 "+msg2.getPool());


            if(n<5) {
                byte[] tmp=os1.toByteArray();

                if (n > 0) {
                    System.out.println("eq=" + Arrays.equals(tmp, bytes3));
                }
                bytes3 = tmp;
                System.gc();
                System.out.println("Pool sizez "+msg2.getPool());

            }
            SampleMessageFast.DataMessage msg3 =  SampleMessageFast.DataMessage.newBuilder();

            byte[] tmp=os1.getBytes();
            int tmpLen = os1.size();
            reader.readItem(msg3,tmp,0,tmpLen);
            //System.out.println("Pool size2 "+msg2.getPool());
            //System.out.println("Pool sizez "+msg3.getPool());

            msg3.release();

            //System.out.println("Pool size3 "+msg2.getPool());
        }
    }

}
