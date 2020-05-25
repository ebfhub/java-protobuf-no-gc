package org.ebfhub.fastprotobuf;

import gnu.trove.list.array.TIntArrayList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FastProtoObjectPool {
    private Map<Class<?>, PoolInstance> pool = new ConcurrentHashMap<>();
    private PoolInstance stringBuilderPool = new PoolInstance(StringBuilder::new);
    private PoolInstance arrayListPool = new PoolInstance(ArrayList::new);

    {
        pool.put(StringBuilder.class,stringBuilderPool);
        pool.put(ArrayList.class,arrayListPool);
    }

    private static final int MAX_INSTANCES=Integer.getInteger("PROTOBUF_POOL_MAX_INSTANCES",1024);
    private static final int DEF_CAPACITY=Integer.getInteger("PROTOBUF_POOL_DEF_CAPACITY",1024);

    @Override
    public String toString() {
        return System.identityHashCode(this)+":"+pool.toString();
    }

    private class PoolInstance
    {
        final List<Object> instances=new ArrayList<>(DEF_CAPACITY);
        Supplier<Object> creator;
        Consumer<Object> cleaner;

        PoolInstance(Supplier<Object> s){
            creator=s;
            cleaner=(a)->{};

        }

        PoolInstance(Class cl){
            if(cl == StringBuilder.class){
                creator=StringBuffer::new;
                cleaner=(a)->{};
            } else if (cl == ArrayList.class){
                creator=ArrayList::new;
                cleaner=(a)->{};
            } else {
                try {
                    @SuppressWarnings("unchecked") Method method = cl.getMethod("create", FastProtoObjectPool.class);
                    creator=()-> {
                        try {
                            return method.invoke(null,FastProtoObjectPool.this);
                        } catch (IllegalAccessException|InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    };

                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                cleaner=(a)->{
                    ((FastProtoMessageBase)a).setRefCount(1);
                };
            }
        }

        void add(Object o){
            synchronized (instances) {
                if(instances.size()<MAX_INSTANCES) {
                    instances.add(o);
                }
            }
        }

        public int size() {
            synchronized (instances) {
                return instances.size();
            }
        }

        @Override
        public String toString() {
            synchronized (instances) {
                return String.valueOf(instances.size());
            }
        }

        Object take(){
            synchronized (instances) {
                int size=instances.size();
                if (size != 0) {
                    Object inst = instances.remove(size - 1);
                    cleaner.accept(inst);
                    return inst;
                }
            }
            return creator.get();
        }
    }

    private void returnOne(Object o) {
        clear(o);
        Class<?> cl = o.getClass();
        PoolInstance l = getPoolInstance(cl);
        l.add(o);
    }

    public void returnSpecific(StringBuilder o) {
        o.setLength(0);
        stringBuilderPool.add(o);
    }

    public void returnSpecific(FastProtoMessage o) {
        clearFastProtoMessage(o);
        Class<?> cl = o.getClass();
        PoolInstance l = getPoolInstance(cl);
        l.add(o);
    }

    private void clearFastProtoMessage(FastProtoMessage o) {
        o.clear();
        o.setRefCount(-100);
    }

    public void returnMessageList(List<? extends FastProtoMessage> o) {
        clearList(o);
        arrayListPool.add(o);
    }

    public void returnSpecific(List<?> o) {
        clearList(o);
        arrayListPool.add(o);
    }

    public void returnSpecific(TIntArrayList o) {
        o.clear();
        arrayListPool.add(o);
    }

    private PoolInstance getPoolInstance(Class<?> cl) {
        PoolInstance l = pool.get(cl);
        if (l == null) {
            // Do null check first, just in case lambda creation isn't optimized
            l = pool.computeIfAbsent(cl, PoolInstance::new);
        }
        return l;
    }
    private void clear(Object o) {
        if( o instanceof FastProtoMessage){
            clearFastProtoMessage((FastProtoMessage) o);
        } else if ( o instanceof List){
            //noinspection rawtypes
            clearList((List)o);
        } else if ( o instanceof TIntArrayList){
            ((TIntArrayList)o).clear();
        } else {
            ((StringBuilder)o).setLength(0);
        }
    }

    private void clearList(List<?> list) {
        for(int size=list.size(),n=0;n<size;n++) {
            Object o = list.get(n);
            if(o instanceof FastProtoWritable){
                ((FastProtoWritable)o).release();
            } else {
                returnOne(o);
            }
        }
        list.clear();
    }

    public <T> T take(Class<T> cl) {
        PoolInstance l = getPoolInstance(cl);

        //noinspection unchecked
        return (T)l.take();
    }

    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> takeList() {
        return take(ArrayList.class);
    }
    public TIntArrayList takeIntList() {
        return take(TIntArrayList.class);
    }
}
