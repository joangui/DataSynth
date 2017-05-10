package org.dama.datasynth.matching;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by aprat on 5/03/17.
 */
public class Distribution<T extends Comparable<T>> {

    public static class Entry<T extends Comparable<T>> {
        private T value;
        private double probability;

        public Entry(T value, double probability) {
            this.value = value;
            this.probability = probability;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public double getProbability() {
            return probability;
        }

        public void setProbability(double probability) {
            this.probability = probability;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj.getClass() != this.getClass()) return false;
            Entry<T> entry = (Entry<T>)obj;
            return value.compareTo(entry.getValue()) == 0 &&
                    probability == entry.getProbability();
        }
    }

    private ArrayList<Entry<T>> entries = new ArrayList<>();

    public void learn(ArrayList<T> values) {

        TreeMap<T,Long> counts = new TreeMap<>();
        long tuplecount = 0L;
        for(T value : values) {
            Long number = null;
            if((number = counts.get(value)) == null) {
                number = 0L;
            }
            counts.put(value,number+1);
            tuplecount+=1;
        }

        for(Map.Entry<T,Long> e : counts.entrySet() ) {
            entries.add(new Entry<T>(e.getKey(), e.getValue() / (double)(tuplecount)));
        }

        checkCorrectness();
    }

    private void checkCorrectness() {
        double sum = 0.0;
        for( Entry e : entries)  {
            sum+=e.probability;
        }
        if(sum > 1.0 ) throw new RuntimeException("probabilities sum more than 1.0");
    }

    public ArrayList<Entry<T>> getEntries() {
        return entries;
    }
}
