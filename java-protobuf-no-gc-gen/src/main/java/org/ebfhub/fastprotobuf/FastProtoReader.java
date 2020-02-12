package org.ebfhub.fastprotobuf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastProtoReader {

    private byte[] bb = new byte[0];

    public ObjectPool getPool() {
        return pool;
    }

    public static class ObjectPool {
        private Map<Class<?>, List<Object>> pool = new HashMap<>();


        public void returnOne(Object o) {
            clear(o);
            Class<?> cl = o.getClass();
            List<Object> l = pool.computeIfAbsent(cl, a -> new ArrayList<>());
            l.add(o);
        }

        private void clear(Object o) {
            if( o instanceof FastProtoSetter){
                ((FastProtoSetter) o).clear(this);
            } else if ( o instanceof List){
                List list = (List)o;
                for(int size=list.size(),n=0;n<size;n++) {
                    returnOne(list.get(n));
                }
                list.clear();

            } else {
                ((StringBuilder)o).setLength(0);
            }
        }

        public void clearList(List<?> list) {
            for(int size=list.size(),n=0;n<size;n++) {
                returnOne(list.get(n));
            }
            list.clear();
        }
        public <T> T take(Class<T> cl) {
            List<Object> l = pool.get(cl);
            if (l == null || l.size() == 0) {
                try {
                    return cl.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new IllegalStateException("can't allocate " + cl, e);
                }
            } else {
                //noinspection unchecked
                return (T)l.remove(l.size() - 1);
            }
        }

        public <T> List<T> takeList() {
            return take(ArrayList.class);
        }
    }

    ObjectPool pool=new ObjectPool();

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
                            StringBuilder sb = setter.field_builder(field,pool);
                            sb.setLength(0);
                            Utf8.getCharsFromUtf8(0,size,sb,UnsafeUtil.ARRAY_BYTE_BASE_OFFSET,bb);
                        }
                        break;
                        case MESSAGE:
                        {
                            int l = is.pushLimit(size);
                            parse(is,setter.field_add(field,pool));
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
}
