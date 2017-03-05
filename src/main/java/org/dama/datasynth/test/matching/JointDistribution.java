package org.dama.datasynth.test.matching;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * Created by aprat on 2/03/17.
 */
public class JointDistribution< XType extends Comparable<XType>,
                                YType extends Comparable<YType>> {

    public static class Entry<  XType extends Comparable<XType>,
                                YType extends Comparable<YType>> {
        private XType    xvalue;
        private YType    yvalue;
        private double   probability;

        public Entry(XType xvalue, YType yvalue, double probability) {
            this.xvalue = xvalue;
            this.yvalue = yvalue;
            this.probability = probability;
        }

        public XType getXvalue() {
            return xvalue;
        }

        public void setXvalue(XType xvalue) {
            this.xvalue = xvalue;
        }

        public YType getYvalue() {
            return yvalue;
        }

        public void setYvalue(YType yvalue) {
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
            Entry<XType,YType> entry = (Entry<XType,YType>)obj;
            return xvalue.compareTo(entry.getXvalue()) == 0 &&
                   yvalue.compareTo(entry.getYvalue()) == 0 &&
                   probability == entry.getProbability();
        }
    }

    private ArrayList<Entry<XType,YType>> entries = new ArrayList<Entry<XType,YType>>();

    /**
     * Loads the Joint probability distribution from a file.
     * The file contains three columns: the x value, the y value and the probability of observing the
     * the x and y values.
     * @param inputStream The stream with the probability distribution
     * @param xparser The parser used to parse values of the x type from string to their native type
     * @param yparser The parser used to parse values of the y type from string to their native type
     */
    public void load( InputStream inputStream, String separator, Function<String, XType> xparser, Function<String, YType> yparser ) {
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
     * @param pairs The collection of pairs to learn from
     */
    public void learn(ArrayList<Tuple<XType,YType>> pairs) {
        TreeMap<Tuple<XType,YType>,Long> counts = new TreeMap<>();
        long tuplecount = 0L;
        for(Tuple<XType,YType> tuple : pairs) {
           Long number = null;
           if((number = counts.get(tuple)) == null) {
               number = 0L;
           }
           counts.put(tuple,number+1);
           tuplecount+=1;
        }

        for(HashMap.Entry<Tuple<XType,YType>,Long> e : counts.entrySet() ) {
           entries.add(new Entry<XType,YType>(e.getKey().getXvalue(),e.getKey().getYvalue(), e.getValue() / (double)(tuplecount)));
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

    public List<Entry<XType,YType>> getEntries() {
        return entries;
    }
}
