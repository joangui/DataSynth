package org.dama.datasynth.runtime.spark.operators.matching.utils;

/**
  * Created by joangui on 10/07/2017.
  */

import org.dama.datasynth.runtime.spark.operators.matching.Table;
import org.dama.datasynth.runtime.spark.operators.matching.Tuple;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;


public class JointDistribution<X extends Comparable<X>, Y extends Comparable<Y>> extends HashMap<Tuple<X, Y>, Double> {


    /**
     * Loads the Joint probability distribution from a file. The file
     * contains three columns: the x value, the y value and the probability
     * of observing the the x and y values.
     *
     * @param inputStream The stream with the probability distribution
     * @param xparser The parser used to parse values of the x type from
     * string to their native type
     * @param yparser The parser used to parse values of the y type from
     * string to their native type
     */
    public void load(InputStream inputStream, String separator, Function<String, X> xparser, Function<String, Y> yparser) {
        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = fileReader.readLine()) != null) {
                String fields[] = line.split(separator);
                put(new Tuple<>(xparser.apply(fields[0]), yparser.apply(fields[1])), Double.parseDouble(fields[2]));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        checkCorrectness();
    }

    /**
     * Learns the Joint probability distribution from a collection of
     * tuples.
     *
     * @param table The collection of pairs to learn from
     */
    public void learn(Table<X, Y> table) {
        TreeMap<Tuple<X, Y>, Long> counts = new TreeMap<>();
        long tuplecount = 0L;
        for (Tuple<X, Y> tuple : table) {
            Long number = null;
            if ((number = counts.get(tuple)) == null) {
                number = 0L;
            }
            counts.put(tuple, number + 1);
            tuplecount += 1;
        }

        for (Map.Entry<Tuple<X, Y>, Long> e : counts.entrySet()) {
            Double probability = e.getValue() / (double) (tuplecount);
            put(new Tuple<>(e.getKey().getX(), e.getKey().getY()), probability);
        }

        checkCorrectness();
    }

    private void checkCorrectness() {
        double sum = 0.0;
        for (Map.Entry<Tuple<X,Y>,Double> e : entrySet()) {
            sum += e.getValue();
        }
        if (Math.abs(sum - 1.0) > 0.0001) {
            throw new RuntimeException("probabilities sum more than 1.0");
        }
    }

    public List<Map.Entry<Tuple<X,Y>, Double>> getEntries() {
        return new ArrayList<>(entrySet());
    }

    public double getProbability(Tuple<X, Y> tuple) {
        Double prob = get(tuple);
        if(prob == null) return 0.0D;
        return prob;
    }
}

