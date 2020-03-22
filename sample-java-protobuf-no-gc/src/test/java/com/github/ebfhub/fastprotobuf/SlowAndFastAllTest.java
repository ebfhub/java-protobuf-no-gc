package com.github.ebfhub.fastprotobuf;

import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessage;
import com.github.ebfhub.fastprotobuf.sample.proto.SampleMessageFast;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import org.ebfhub.fastprotobuf.*;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SlowAndFastAllTest {
    @Test
    public void testNewReadsOld() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SampleMessage.AllTypes oldMsg = SampleMessage.AllTypes.newBuilder()
                .setBool(true)
                .setString("hello")
                .setFloat(98.97f)
                .setDouble(98.99)
                .setInt32(77)
                .setInt64(1000000000)
                .setSint32(77)
                .setSint64(2000000000)
                .setSfixed32(77)
                .setSfixed64(3000000000L)
                .setUint32(77)
                .setUint64(4000000000L)
                .setFixed32(77)
                .setFixed64(5000000000L)


                .setNull( SampleMessage.NullValue.newBuilder())
                .setStringList(SampleMessage.StringList.newBuilder()
                        .addStrings("hello").addStrings("world")
                        .build())
                .build();

        SampleMessageFast.AllTypes newMsg = copyThrough(oldMsg);


        compareResults(oldMsg, newMsg);

    }


    @Test
    public void testNewReadsOldNeg() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SampleMessage.AllTypes oldMsg = SampleMessage.AllTypes.newBuilder()
                .setBool(false)
                .setFloat(-98.97f)
                .setDouble(-98.99)
                .setInt32(-77)
                .setInt64(-1000000000)
                .setSint32(-77)
                .setSint64(-2000000000)
                .setSfixed32(-77)
                .setSfixed64(-3000000000L)
                .setUint32(-77)
                .setUint64(-4000000000L)
                .setFixed32(-77)
                .setFixed64(-5000000000L)


                .build();

        SampleMessageFast.AllTypes newMsg = copyThrough(oldMsg);


        compareResults(oldMsg, newMsg);

    }

    private void compareResults(Object oldMsg, Object newMsg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method[] oldMs = oldMsg.getClass().getDeclaredMethods();
        for(Method oldM:oldMs){
            String name = oldM.getName();
            if(includeGetter(name)) {
                Method newM = newMsg.getClass().getMethod(name, new Class[0]);
                Object oldV = repair(oldM.getReturnType(),oldM.invoke(oldMsg, new Object[0]));
                Object newV = repair(newM.getReturnType(),newM.invoke(newMsg, new Object[0]));
                assertEquals(name, oldV, newV);
            }
        }
    }

    private boolean includeGetter(String name) {
        return name.startsWith("get") && !name.matches("getClass|getDescriptor|getParserForType|getDefaultInstanceForType|getDefaultInstance|getUnknownFields|getStringBytes|getSerializedSize|.*OrBuilder");
    }

    private Object repair(Class cl, Object v) {
        if(v instanceof SampleMessage.StringList){
            return getSList((SampleMessage.StringList)v);
        }
        if(v instanceof SampleMessageFast.StringList){
            return getSList((SampleMessageFast.StringList)v);
        }
        if( cl == SampleMessage.NullValue.class){
            return( getIsNull((SampleMessage.NullValue)v));
        }
        if( cl == SampleMessageFast.NullValue.class){
            return( getIsNull((SampleMessageFast.NullValue)v));
        }
        if(v instanceof CharSequence){
            return v.toString();
        }
        return v;
    }

    private SampleMessageFast.AllTypes copyThrough(SampleMessage.AllTypes oldMsg) throws IOException {
        byte[] bytes=oldMsg.toByteArray();
        System.out.println(ProtoDebug.decodeProto(bytes,false));

        CodedInputStream is=CodedInputStream.newInstance(bytes);

        FastProtoReader reader = new FastProtoReader();
        FastProtoObjectPool pool = reader.getPool();
        FastProtoWriter writer=new FastProtoWriter();


        SampleMessageFast.AllTypes newMsg = SampleMessageFast.AllTypes.create(pool);
        reader.parse(is,newMsg.getSetter());


        ReusableByteArrayOutputStream os1 = new ReusableByteArrayOutputStream();
        CodedOutputStream o2 =  CodedOutputStream.newInstance(os1);
        newMsg.write(o2,writer);
        o2.flush();
        return newMsg;
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
