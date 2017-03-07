package org.dama.datasynth.test.matching;

import java.io.*;
import java.util.*;
import java.util.function.Function;

/**
 * Created by aprat on 2/03/17.
 */
public class JointDistribution< X extends Comparable<X>,
                                Y extends Comparable<Y>> {

    public static class Entry<  X extends Comparable<X>,
                                Y extends Comparable<Y>> {
        private X xvalue;
        private Y yvalue;
        private double   probability;

        public Entry(X xvalue, Y yvalue, double probability) {
            this.xvalue = xvalue;
            this.yvalue = yvalue;
            this.probability = probability;
        }

        public X getXvalue() {
            return xvalue;
        }

        public void setXvalue(X xvalue) {
            this.xvalue = xvalue;
        }

        public Y getYvalue() {
            return yvalue;
        }

        public void setYvalue(Y yvalue) {
            this.yvalue = yvalue;
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
            Entry<X, Y> entry = (Entry<X, Y>)obj;
            return xvalue.compareTo(entry.getXvalue()) == 0 &&
                   yvalue.compareTo(entry.getYvalue()) == 0 &&
                   probability == entry.getProbability();
        }
    }

    private ArrayList<Entry<X, Y>> entries = new ArrayList<Entry<X, Y>>();

    /**
     * Loads the Joint probability distribution from a file.
     * The file contains three columns: the x value, the y value and the probability of observing the
     * the x and y values.
     * @param inputStream The stream with the probability distribution
     * @param xparser The parser used to parse values of the x type from string to their native type
     * @param yparser The parser used to parse values of the y type from string to their native type
     */
    public void load(InputStream inputStream, String separator, Function<String, X> xparser, Function<String, Y> yparser ) {
        try {
            BufferedReader fileReader =  new BufferedReader( new InputStreamReader(inputStream));
            String line = null;
            while( (line = fileReader.readLine()) != null) {
                String fields [] = line.split(separator);
                entries.add( new Entry(xparser.apply(fields[0]), yparser.apply(fields[1]), Double.parseDouble(fields[2])));
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        checkCorrectness();
    }

    /**
     * Learns the Joint probability distribution from a collection of tuples.
     *
     * @param table The collection of pairs to learn from
     */
    public void learn(Table<X, Y> table) {
        TreeMap<Tuple<X, Y>,Long> counts = new TreeMap<>();
        long tuplecount = 0L;
        for(Tuple<X, Y> tuple : table) {
           Long number = null;
           if((number = counts.get(tuple)) == null) {
               number = 0L;
           }
           counts.put(tuple,number+1);
           tuplecount+=1;
        }

        for(Map.Entry<Tuple<X, Y>,Long> e : counts.entrySet() ) {
           entries.add(new Entry<X, Y>(e.getKey().getX(),e.getKey().getY(), e.getValue() / (double)(tuplecount)));
        }

        checkCorrectness();
    }

    private void checkCorrectness() {
        double sum = 0.0;
        for( Entry e : entries)  {
            sum+=e.probability;
        }
        if(Math.abs(sum - 1.0) > 0.0001 ) throw new RuntimeException("probabilities sum more than 1.0");
    }

    public List<Entry<X, Y>> getEntries() {
        return entries;
    }
}
