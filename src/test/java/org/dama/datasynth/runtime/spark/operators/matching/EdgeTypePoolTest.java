package org.dama.datasynth.runtime.spark.operators.matching;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 3/03/17.
 */
public class EdgeTypePoolTest {

    @Test
    public void testPickRandomEdge() {
        String str = new String( "Male Female 0.7\n" +
                "Male Male 0.15\n" +
                "Female Female 0.15");

        InputStream stream = new ByteArrayInputStream(str.getBytes());
        JointDistribution<String, String> distribution = new JointDistribution<String,String>();
        distribution.load(stream," ", (String s) -> s, (String s) -> s );

        EdgeTypePool<String, String> edgeTypePool = new EdgeTypePool(distribution,10000L,123456789L);

        HashMap<Integer, Integer> counts = new HashMap<Integer,Integer>();
        counts.put(0,0);
        counts.put(1,0);
        counts.put(2,0);

        int numberObserved = 0;
        for(int i = 0; i < 10000; i+=1, numberObserved+=1) {
            Tuple<String,String> entry = edgeTypePool.pickRandomEdge();
            if(entry != null) {
                if (entry.getX().compareTo("Male") == 0 && entry.getY().compareTo("Female") == 0) {
                    counts.put(0, counts.get(0) + 1);
                    continue;
                }

                if (entry.getX().compareTo("Male") == 0 && entry.getY().compareTo("Male") == 0) {
                    counts.put(1, counts.get(1) + 1);
                    continue;
                }

                if (entry.getX().compareTo("Female") == 0 && entry.getY().compareTo("Female") == 0) {
                    counts.put(2, counts.get(2) + 1);
                    continue;
                }
            }
        }

        assertTrue(Math.abs(numberObserved - (double) 10000 ) <= 3);
        assertTrue(Math.abs(counts.get(0)/(double)10000 - 0.7) < 0.001);
        assertTrue(Math.abs(counts.get(1)/(double)10000 - 0.15) < 0.001);
        assertTrue(Math.abs(counts.get(2)/(double)10000 - 0.15) < 0.001);
    }

    @Test
    public void testPickRandomEdgeTail() {
        String str = new String( "Male Female 0.7\n" +
                "Male Male 0.15\n" +
                "Female Female 0.15");

        InputStream stream = new ByteArrayInputStream(str.getBytes());
        JointDistribution<String, String> distribution = new JointDistribution<String,String>();
        distribution.load(stream," ", (String s) -> s, (String s) -> s );

        EdgeTypePool<String, String> edgeTypePool = new EdgeTypePool(distribution,10000L,123456789L);

        HashMap<Integer, Integer> counts = new HashMap<Integer,Integer>();
        counts.put(0,0);
        counts.put(1,0);
        counts.put(2,0);

        int numberObserved = 0;
        for(int i = 0; i < 10000; i+=1) {
            Tuple<String,String> entry = edgeTypePool.pickRandomEdgeTail("Male");
            if(entry != null) {
                if (entry.getX().compareTo("Male") == 0 && entry.getY().compareTo("Female") == 0) {
                    numberObserved+=1;
                    counts.put(0, counts.get(0) + 1);
                    continue;
                }

                if (entry.getX().compareTo("Male") == 0 && entry.getY().compareTo("Male") == 0) {
                    numberObserved+=1;
                    counts.put(1, counts.get(1) + 1);
                    continue;
                }
            }
        }

        for(int i = 0; i < 10000; i+=1) {
            Tuple<String,String> entry = edgeTypePool.pickRandomEdgeTail("Female");
            if(entry != null) {
                if (entry.getX().compareTo("Female") == 0 && entry.getY().compareTo("Female") == 0) {
                    numberObserved+=1;
                    counts.put(2, counts.get(2) + 1);
                    continue;
                }
            }
        }

        assertTrue(Math.abs(numberObserved - (double) 10000 ) <= 3);
        assertTrue(Math.abs(counts.get(0)/(double)10000 - 0.7) < 0.001);
        assertTrue(Math.abs(counts.get(1)/(double)10000 - 0.15) < 0.001);
        assertTrue(Math.abs(counts.get(2)/(double)10000 - 0.15) < 0.001);
    }

    @Test
    public void testPickRandomEdgeHead() {
        String str = new String( "Male Female 0.7\n" +
                "Male Male 0.15\n" +
                "Female Female 0.15");

        InputStream stream = new ByteArrayInputStream(str.getBytes());
        JointDistribution<String, String> distribution = new JointDistribution<String,String>();
        distribution.load(stream," ", (String s) -> s, (String s) -> s );

        EdgeTypePool<String, String> edgeTypePool = new EdgeTypePool(distribution,10000L,123456789L);

        HashMap<Integer, Integer> counts = new HashMap<Integer,Integer>();
        counts.put(0,0);
        counts.put(1,0);
        counts.put(2,0);

        int numberObserved = 0;
        for(int i = 0; i < 10000; i+=1) {
            Tuple<String,String> entry = edgeTypePool.pickRandomEdgeHead("Male");
            if(entry != null) {

                if (entry.getX().compareTo("Male") == 0 && entry.getY().compareTo("Male") == 0) {
                    numberObserved+=1;
                    counts.put(1, counts.get(1) + 1);
                    continue;
                }

            }
        }

        for(int i = 0; i < 10000; i+=1) {
            Tuple<String,String> entry = edgeTypePool.pickRandomEdgeHead("Female");
            if(entry != null) {
                if (entry.getX().compareTo("Female") == 0 && entry.getY().compareTo("Female") == 0) {
                    numberObserved+=1;
                    counts.put(2, counts.get(2) + 1);
                    continue;
                }
                if (entry.getX().compareTo("Male") == 0 && entry.getY().compareTo("Female") == 0) {
                    numberObserved+=1;
                    counts.put(0, counts.get(0) + 1);
                    continue;
                }
            }
        }

        assertTrue(Math.abs(numberObserved - (double) 10000 ) <= 3);
        assertTrue(Math.abs(counts.get(0)/(double)10000 - 0.7) < 0.001);
        assertTrue(Math.abs(counts.get(1)/(double)10000 - 0.15) < 0.001);
        assertTrue(Math.abs(counts.get(2)/(double)10000 - 0.15) < 0.001);
    }
}
