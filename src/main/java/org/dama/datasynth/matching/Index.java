package org.dama.datasynth.matching;

import java.util.*;

/**
 * Created by aprat on 3/03/17.
 */
public class Index <T extends Comparable<T> > {

    private HashMap<T,LinkedList<Long>> data = new HashMap<>();

    public Index(ArrayList<Tuple<Long,T>> array) {
        for(Tuple<Long,T> entry : array ) {
            LinkedList<Long> container = null;
            if((container = data.get(entry.getY())) == null) {
                container = new LinkedList<Long>();
                data.put(entry.getY(),container);
            }
            container.add(entry.getX());
        }
    }

    public Set<T> values() {
        return data.keySet();
    }

    public Long poll(T value) {
        LinkedList<Long> ids = data.get(value);
        if(ids == null) {
            return null;
        }
        Long ret = ids.pollFirst();
        if(ids.size() == 0) {
            data.remove(value);
        }
        return ret;
    }

    public List<Long> getIds(T value) {
        return data.get(value);
    }

    /*public Long random() {
        int next = random.nextInt(data.size());
        ArrayList<T> keys = new ArrayList<>(data.keySet());
        Collections.shuffle(keys,random);
        T key = keys.get(next);
        return data.get(key).pollFirst();
    }*/

}
