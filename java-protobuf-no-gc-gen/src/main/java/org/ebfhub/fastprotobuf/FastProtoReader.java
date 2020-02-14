package org.ebfhub.fastprotobuf;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.WireFormat;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @return a {@link org.ebfhub.fastprotobuf.FastProtoReader.ObjectPool} object.
     */
    public ObjectPool getPool() {
        return pool;
    }

    public static class ObjectPool {
        private Map<Class<?>, List<Object>> pool = new HashMap<>();
        private List<Object> stringBuilderPool = new ArrayList<>();
        private List<Object> arrayListPool = new ArrayList<>();

        {
            pool.put(StringBuilder.class,stringBuilderPool);
            pool.put(ArrayList.class,arrayListPool);
        }

        public void returnOne(Object o) {
            clear(o);
            Class<?> cl = o.getClass();
            List<Object> l = pool.computeIfAbsent(cl, a -> new ArrayList<>());
            l.add(o);
        }
        public void returnSpecific(StringBuilder o) {
            o.setLength(0);
            stringBuilderPool.add(o);
        }
        public void returnSpecific(FastProtoSetter o) {
            o.clear(this);
            Class<?> cl = o.getClass();
            List<Object> l = pool.computeIfAbsent(cl, a -> new ArrayList<>());
            l.add(o);
        }

        public void returnSpecific(List<?> o) {
            clearList(o);
            arrayListPool.add(o);
        }

        public void returnSpecific(TIntArrayList o) {
            o.clear();
            arrayListPool.add(o);
        }
        private void clear(Object o) {
            if( o instanceof FastProtoSetter){
                ((FastProtoSetter) o).clear(this);
            } else if ( o instanceof List){
                //noinspection rawtypes
                clearList((List)o);
            } else if ( o instanceof TIntArrayList){
                ((TIntArrayList)o).clear();
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

        @SuppressWarnings("unchecked")
        public <T> ArrayList<T> takeList() {
            return take(ArrayList.class);
        }
        public TIntArrayList takeIntList() {
            return take(TIntArrayList.class);
        }
    }

    ObjectPool pool=new ObjectPool();
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
                            StringBuilder sb = setter.field_builder(field,pool);
                            sb.setLength(0);
                            Utf8.getCharsFromUtf8(0, size, sb, 0, bb, provider);
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
