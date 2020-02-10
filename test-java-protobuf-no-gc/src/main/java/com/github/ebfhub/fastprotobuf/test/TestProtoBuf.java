package com.github.ebfhub.fastprotobuf.test;

import com.github.ebfhub.fastprotobuf.test.proto.TestMessage;
import com.github.ebfhub.fastprotobuf.test.proto.TestMessageFast;
import com.google.common.base.Charsets;
import com.google.protobuf.*;
import com.github.ebfhub.fastprotobuf.FastProtoReader;
import com.github.ebfhub.fastprotobuf.FastProtoWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class TestProtoBuf {
    public static void main(String[]args) throws IOException, NoSuchFieldException, NoSuchMethodException {

        TestMessage.Message msg = TestMessage.Message.newBuilder()
                .setSymbol("sym1")
                .setSymbolId(12)
                .setTs(System.currentTimeMillis())
                    .addValues(TestMessage.FieldAndValue.newBuilder()
                        .setField(123).setBool(true))
                    .addValues(TestMessage.FieldAndValue.newBuilder()
                        .setField(126).setString("hello"))
                    .addValues(TestMessage.FieldAndValue.newBuilder()
                        .setField(124).setFieldName("myField").setInt32(97))
                    .addValues(TestMessage.FieldAndValue.newBuilder()
                        .setField(190).setFloat(98.97f))
                    .addValues(TestMessage.FieldAndValue.newBuilder()
                        .setField(126).setDouble(98.99)
                            .build())
                .build();


        byte[] bytes=msg.toByteArray();
        System.out.println("Bytes = "+new String(bytes));
        System.out.println(ProtoDebug.decodeProto(bytes,false));


        CodedInputStream is=CodedInputStream.newInstance(bytes);


        FastProtoReader reader = new FastProtoReader();
        TestMessageFast.Message msg1 = new TestMessageFast.Message();
        reader.parse(is,msg1);


        ByteArrayOutputStream os1 = new ByteArrayOutputStream();
        CodedOutputStream o2 =  CodedOutputStream.newInstance(os1);
        FastProtoWriter writer=new FastProtoWriter();
        msg1.write(o2,writer);
        o2.flush();


        byte[] bytes2=os1.toByteArray();

        byte[] bytes3=null;
        TestMessageFast.Message msg2 = new TestMessageFast.Message();
        TestMessageFast.Message msg3 = new TestMessageFast.Message();
        msg2.setSymbol("bye");

        for(int k=0;k<10;k++){
            TestMessageFast.FieldAndValue val = msg2.addValues();
            val.set_string("fifty"+k);
            val.set_bool(true);
            val.setField(k);
            val.setFieldName("jonb"+k);
        }


        for(int n=0;n<3;n++) {
            msg2.clear();

            msg2.setSymbol("hello1");
            msg2.setTs(System.currentTimeMillis());
            msg2.setSymbolId(123);
            TestMessageFast.FieldAndValue val = msg2.addValues();
            val.set_string("fifty");
            val.setField(1000);
            val.setFieldName("jonb");


            os1.reset();
            msg1.write(o2,writer);
            o2.flush();
            byte[] tmp=os1.toByteArray();
            if(n>0){
                System.out.println("eq="+Arrays.equals(tmp,bytes3)+":"+new String(tmp));
            }
            bytes3=tmp;

            CodedInputStream is3=CodedInputStream.newInstance(tmp);

            msg3.clear();
            reader.parse(is3,msg3);
        }


    }

    static class ProtoDebug
    {
        static String INDENT="   ";
        public static String decodeProto(byte[] data, boolean singleLine) throws IOException {
            return decodeProto(ByteString.copyFrom(data), 0, singleLine);
        }

        public static String decodeProto(ByteString data, int depth, boolean singleLine) throws IOException {
            final CodedInputStream input = CodedInputStream.newInstance(data.asReadOnlyByteBuffer());
            return decodeProtoInput(input, depth, singleLine);
        }

        private static String decodeProtoInput(CodedInputStream input, int depth, boolean singleLine) throws IOException {
            StringBuilder s = new StringBuilder("{ ");
            boolean foundFields = false;
            while (true) {
                final int tag = input.readTag();
                int type = WireFormat.getTagWireType(tag);
                if (tag == 0 || type == WireFormat.WIRETYPE_END_GROUP) {
                    break;
                }
                foundFields = true;
                protoNewline(depth, s, singleLine);

                final int number = WireFormat.getTagFieldNumber(tag);
                s.append(number).append(": ");

                switch (type) {
                    case WireFormat.WIRETYPE_VARINT:
                        s.append(input.readInt64());
                        break;
                    case WireFormat.WIRETYPE_FIXED64:
                        s.append(Double.longBitsToDouble(input.readFixed64()));
                        break;
                    case WireFormat.WIRETYPE_LENGTH_DELIMITED:
                        ByteString data = input.readBytes();
                        try {
                            String submessage = decodeProto(data, depth + 1, singleLine);
                            if (data.size() < 30) {
                                boolean probablyString = true;
                                String str = new String(data.toByteArray(), Charsets.UTF_8);
                                for (char c : str.toCharArray()) {
                                    if (c < '\n') {
                                        probablyString = false;
                                        break;
                                    }
                                }
                                if (probablyString) {
                                    s.append("\"").append(str).append("\" ");
                                }
                            }
                            s.append(submessage);
                        } catch (IOException e) {
                            s.append('"').append(new String(data.toByteArray())).append('"');
                        }
                        break;
                    case WireFormat.WIRETYPE_START_GROUP:
                        s.append(decodeProtoInput(input, depth + 1, singleLine));
                        break;
                    case WireFormat.WIRETYPE_FIXED32:
                        s.append(Float.intBitsToFloat(input.readFixed32()));
                        break;
                    default:
                        throw new InvalidProtocolBufferException("Invalid wire type");
                }

            }
            if (foundFields) {
                protoNewline(depth - 1, s, singleLine);
            }
            return s.append('}').toString();
        }

        private static void protoNewline(int depth, StringBuilder s, boolean noNewline) {
            if (noNewline) {
                s.append(" ");
                return;
            }
            s.append('\n');
            for (int i = 0; i <= depth; i++) {
                s.append(INDENT);
            }
        }

    }
}
