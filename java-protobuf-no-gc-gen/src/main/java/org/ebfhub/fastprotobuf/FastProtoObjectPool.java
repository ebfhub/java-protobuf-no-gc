package org.ebfhub.fastprotobuf;

import gnu.trove.list.array.TIntArrayList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FastProtoObjectPool {
    private Map<Class<?>, PoolInstance> pool = new HashMap<>();
    private PoolInstance stringBuilderPool = new PoolInstance(StringBuilder::new);
    private PoolInstance arrayListPool = new PoolInstance(ArrayList::new);

    {
        pool.put(StringBuilder.class,stringBuilderPool);
        pool.put(ArrayList.class,arrayListPool);
    }

    private class PoolInstance
    {
        List<Object> instances=new ArrayList<>();
        Supplier<Object> creator;
        void add(Object o){
            instances.add(o);
        }
        PoolInstance(Supplier<Object> s){
            creator=s;
        }

        PoolInstance(Class cl){
            if(cl == StringBuilder.class){
                creator=StringBuffer::new;
            } else if (cl == ArrayList.class){
                creator=ArrayList::new;
            } else {
                try {
                    Method method = cl.getMethod("create", FastProtoObjectPool.class);
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
            }
        }

        public int size() {
            return instances.size();
        }

        Object take(){
            if(instances.size()==0){
                return creator.get();
            }
            return instances.remove(instances.size()-1);
        }
    }

    public void returnOne(Object o) {
        clear(o);
        Class<?> cl = o.getClass();
        PoolInstance l = pool.computeIfAbsent(cl, a -> new PoolInstance(cl));
        l.add(o);
    }
    public void returnSpecific(StringBuilder o) {
        o.setLength(0);
        stringBuilderPool.add(o);
    }
    public void returnSpecific(FastProtoSetter o) {
        o.clear();
        Class<?> cl = o.getClass();
        PoolInstance l = pool.computeIfAbsent(cl, a -> new PoolInstance(cl));
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
            ((FastProtoSetter) o).clear();
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
        PoolInstance l = pool.get(cl);
        if(l==null){
            pool.put(cl, l=new PoolInstance(cl));
        }

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
