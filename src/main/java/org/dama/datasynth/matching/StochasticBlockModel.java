package org.dama.datasynth.matching;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by aprat on 10/03/17.
 */
public class StochasticBlockModel <T> {

    private double [][] probabilities = null;
    private long [] sizes = null;
    private Map<T,Integer> mapping = null;

    public StochasticBlockModel(Map<T,Integer> mapping, long [] sizes, double [][] probabilities) {
        if(probabilities.length != sizes.length) throw new RuntimeException("sizes vector and probabilities matrix side missmatch");
        this.probabilities = probabilities;
        this.sizes = sizes;
        for(Map.Entry<T,Integer> entry : mapping.entrySet()) {
            if(entry.getValue() >= sizes.length) {
                throw new RuntimeException("Mapping between values and indices exceeds range");
            }
        }
        this.mapping = mapping;
    }

    public long getNumBlocks() {
        return sizes.length;
    }

    public long getSize(T value) {
        return sizes[mapping.get(value)];
    }

    public double getProbability( T i, T j) {
        int mapi = mapping.get(i);
        int mapj = mapping.get(j);
        return mapi < mapj ? probabilities[mapi][mapj] : probabilities[mapj][mapi];
    }

    public static <T extends Comparable<T>> StochasticBlockModel<T> extractFrom( long numEdges, Table<Long,T> values,
                                                                                 JointDistribution<T,T> distribution ) {

        Index<T> index = new Index(values);
        Map<T,Integer> mapping = new TreeMap<>();
        long [] sizes = new long[index.values().size()];
        int i = 0;
        for(T value : index.values()) {
            mapping.put(value,i);
            sizes[i] = index.getIds(value).size();
            i+=1;
        }

        double [][] probabilities = new double[sizes.length][sizes.length];
        for(i = 0; i < sizes.length; i+=1) {
            Arrays.fill(probabilities[i], 0.0D);
        }
        for(T value1 : index.values()) {
            i = mapping.get(value1);
            for( T value2 : index.values()) {
                int j = mapping.get(value2);
                if(i == j) {
                    probabilities[i][j] = 2.0D*numEdges*distribution.getProbability(new Tuple(value1,value2))/(sizes[i]*(sizes[j]-1));
                } else if( i < j) {
                    if(value1.compareTo(value2) < 0) {
                        probabilities[i][j] = numEdges * distribution.getProbability(new Tuple(value1, value2)) / (sizes[i] * (sizes[j]));
                    } else {
                        probabilities[i][j] = numEdges * distribution.getProbability(new Tuple(value2, value1)) / (sizes[i] * (sizes[j]));
                    }
                }
            }
        }
        return new StochasticBlockModel<T>(mapping,sizes,probabilities);
    }
}
