package org.ebfhub.fastprotobuf;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class MutableHelper
{
    int version;
    long val;
    int kp;
}
class FieldToPosBad  {
    final HashMap<String,Integer> pos = new HashMap<>();

    int getPos(String key){
        Integer p = pos.get(key);
        if(p==null){
            // never, once the process is running
            synchronized (pos) {
                pos.put(key, p = pos.size());
            }
        }
        return p;
    }
}

interface FieldToPos
{
    int get(String key);
    int getOrDefault(String key, int def);
    boolean contains(String key);
    int size();
    boolean fixed();

}
class GrowingFieldToPos implements FieldToPos  {
    ConcurrentHashMap<String,Integer> pos = new ConcurrentHashMap<>();//shared

    @Override
    public int get(String key){

        Integer p = pos.get(key);
        if(p==null){
            Integer r = pos.putIfAbsent(key,p=pos.size());
            if(r!=null){
                p=r;
            }
        }
        return p;
    }

    @Override
    public int getOrDefault(String key, int def) {
        Integer p = pos.get(key);
        return p==null?def:p;
    }

    @Override
    public boolean contains(String key) {
        return pos.contains(key);
    }

    @Override
    public int size() {
        return pos.size();
    }

    @Override
    public boolean fixed() {
        return false;
    }
}


public class TxnMap {

    List<MutableHelper> items=new ArrayList<>();
    FieldToPos pos ;//shared
    List<String> keys=new ArrayList<>();
    int version;

    TxnMap(FieldToPos p){
        this.pos=p;
        this.keys=new ArrayList<>(p.fixed()?p.size():Math.max(16,p.size()));
    }
    void put(String key, int val){
        MutableHelper m = get(key);
        m.val=val;
        upd( key, m);
    }

    private void upd(String key, MutableHelper m) {
        m.version=version;
        if(m.kp!=keys.size()-1){
            keys.remove(m.kp);
            m.kp=keys.size();
            keys.add(key);
        }
    }

    boolean changedSince(String key, int t){
        MutableHelper m = getOrNull(key);
        return m!=null && m.version>=t;
    }


    private MutableHelper getOrNull(String key) {
        int p = pos.getOrDefault(key,-1);
        if(p==-1 ||p>=items.size()){
            return null;
        }
        MutableHelper m = items.get(p);
        return m;

    }
    private MutableHelper get(String key) {
        int p = pos.get(key);
        while(p>=items.size()){
            items.add(null);
        }
        MutableHelper m = items.get(p);
        if(m==null){
            items.set(p,m=new MutableHelper());
            m.kp=keys.size();
            keys.add(key);
        }
        return m;
    }
}
