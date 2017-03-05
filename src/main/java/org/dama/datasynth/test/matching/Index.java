package org.dama.datasynth.test.matching;

import java.util.*;

/**
 * Created by aprat on 3/03/17.
 */
public class Index <T extends Comparable<T> > {

    private HashMap<T,LinkedList<Integer>> data = new HashMap<T,LinkedList<Integer>>();

    public Index(ArrayList<T> array) {
        int arraySize = array.size();
        int i = 0;
        for(T entry : array ) {
            LinkedList<Integer> container = null;
            if((container = data.get(array.get(i))) == null) {
                container = new LinkedList<Integer>();
                data.put(entry,container);
            }
            container.add(i);
           i+=1;
        }
    }

    public Set<T> values() {
        return data.keySet();
    }

    public Integer poll(T value) {
        LinkedList<Integer> ids = data.get(value);
        if(ids == null) {
            return null;
        }
        Integer ret = ids.pollFirst();
        if(ids.size() == 0) {
            data.remove(value);
        }
        return ret;
    }

    public Integer random() {
        Random random = new Random(data.size());
        int next = random.nextInt();
        ArrayList<T> keys = new ArrayList<>(data.keySet());
        Collections.shuffle(keys,random);
        T key = keys.get(next);
        return data.get(key).pollFirst();
    }

}
