package com.github.ebfhub.fastprotobuf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;

public class FastProtoReader {

    private StringBuilder sb = new StringBuilder();
    private byte[] bb = new byte[0];

    public void parse(CodedInputStream is, FastProtoSetter setter) throws java.io.IOException {
        while(!is.isAtEnd()) {
            int tag = is.readTag();
            int wt = WireFormat.getTagWireType(tag);
            int field = WireFormat.getTagFieldNumber(tag);
            FastProtoField fd = setter.getField(field);
            WireFormat.FieldType lt = fd.ft;
            switch(wt){
                case WireFormat.WIRETYPE_VARINT:
                {
                    long value = is.readInt64();
                    switch(lt){
                        case INT32:
                            setter.field_set(field,(int)value);
                            break;
                        case INT64:
                            setter.field_set(field,value);
                            break;
                        case BOOL:
                            setter.field_set(field, value != 0);
                            break;
                        default: throw new UnsupportedOperationException();
                    }
                }
                break;
                case WireFormat.WIRETYPE_FIXED32:
                {
                    int value = is.readRawLittleEndian32();
                    switch(lt){
                        case FLOAT:
                            float f = Float.intBitsToFloat(value);
                            setter.field_set(field,f);
                            break;
                        default: throw new UnsupportedOperationException();
                    }
                }
                break;
                case WireFormat.WIRETYPE_FIXED64:
                {
                    long value = is.readRawLittleEndian64();
                    switch(lt){
                        case DOUBLE:
                            double f = Double.longBitsToDouble(value);
                            setter.field_set(field,f);
                            break;
                        default: throw new UnsupportedOperationException();
                    }
                }
                break;
                case WireFormat.WIRETYPE_LENGTH_DELIMITED:
                {
                    final int size = is.readRawVarint32();
                    if(size<0){
                        throw new IllegalStateException();
                    }
                    switch(lt){
                        case STRING:
                        {
                            sb.setLength(0);
                            if(bb.length<size){
                                bb=new byte[size];
                            }
                            for(int n=0;n<size;n++) {
                                byte b = is.readRawByte();
                                bb[n]=b;
                            }
                            Utf8.getCharsFromUtf8(0,size,sb,UnsafeUtil.ARRAY_BYTE_BASE_OFFSET,bb);
                            setter.field_set(field,sb);
                        }
                        break;
                        case MESSAGE:
                        {
                            int l = is.pushLimit(size);
                            parse(is,setter.field_add(field));
                            is.popLimit(l);
                        }
                        break;
                        default: throw new UnsupportedOperationException();
                    }
                }
                break;
                case WireFormat.WIRETYPE_START_GROUP:
                    break;
                case WireFormat.WIRETYPE_END_GROUP:
                    break;
                default: throw new UnsupportedOperationException();
            }
        }
    }
}
