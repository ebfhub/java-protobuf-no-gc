package org.ebfhub.fastprotobuf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;

import java.io.IOException;

/**
 * <p>FastProtoReader class.</p>
 *
 * @author mac
 * @version $Id: $Id
 */
public class FastProtoReader {

    private byte[] bb = new byte[0];

    /**
     * <p>Getter for the field <code>pool</code>.</p>
     *
     * @return a {@link FastProtoObjectPool} object.
     */
    public FastProtoObjectPool getPool() {
        return pool;
    }

    FastProtoObjectPool pool=new FastProtoObjectPool();
    Utf8.ByteProvider provider = new Utf8.ByteProvider() {
        @Override
        byte getByte(Object src, long offset) {
            return ((byte[])src)[(int)offset];
        }
    };

    /**
     * <p>parse.</p>
     *
     * @param is a {@link com.google.protobuf.CodedInputStream} object.
     * @param setter a {@link org.ebfhub.fastprotobuf.FastProtoSetter} object.
     * @throws java.io.IOException if any.
     */
    public void parse(CodedInputStream is, FastProtoSetter setter) throws java.io.IOException {
        while(!is.isAtEnd()) {
            int tag = is.readTag();
            int wt = WireFormat.getTagWireType(tag);
            int field = WireFormat.getTagFieldNumber(tag);
            FastProtoField fd = setter.field_getDef(field);
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
                    if (lt == WireFormat.FieldType.FLOAT) {
                        float f = Float.intBitsToFloat(value);
                        setter.field_set(field, f);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                }
                break;
                case WireFormat.WIRETYPE_FIXED64:
                {
                    long value = is.readRawLittleEndian64();
                    if (lt == WireFormat.FieldType.DOUBLE) {
                        double f = Double.longBitsToDouble(value);
                        setter.field_set(field, f);
                    } else {
                        throw new UnsupportedOperationException();
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
                            if(bb.length<size){
                                bb=new byte[size];
                            }
                            for(int n=0;n<size;n++) {
                                byte b = is.readRawByte();
                                bb[n]=b;
                            }
                            StringBuilder sb = setter.field_builder(field);
                            sb.setLength(0);
                            Utf8.getCharsFromUtf8(0, size, sb, 0, bb, provider);
                        }
                        break;
                        case MESSAGE:
                        {
                            int l = is.pushLimit(size);
                            parse(is,setter.field_add(field).getSetter());
                            is.popLimit(l);
                        }
                        break;
                        default: throw new UnsupportedOperationException();
                    }
                }
                break;

                case WireFormat.WIRETYPE_START_GROUP:
                case WireFormat.WIRETYPE_END_GROUP:
                    break;

                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    MutableByteArrayInputStream mis = new MutableByteArrayInputStream();
    CodedInputStream is3=CodedInputStream.newInstance(mis);

    public void readItem(FastProtoMessage val, byte[] b, int offset, int len) throws IOException {
        val.clear();
        mis.setBytes(b,offset,len);

        parse(is3,val.getSetter());
        is3.resetSizeCounter();
    }
}
