package org.dama.datasynth.test.matching;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import java.util.*;

/**
 * Created by aprat on 3/03/17.
 */
public class EdgeTypePool< XType extends Comparable<XType>,
                           YType extends Comparable<YType>> {

    // fields
    //private LinkedList<Entry> entries = new LinkedList<Entry>();
    private LinkedListMultimap<XType,YType> tails = LinkedListMultimap.create();
    private ArrayListMultimap<YType,XType> heads = ArrayListMultimap.create();
    private TreeMap<Tuple<XType,YType>,Long> toRemoveTails = new TreeMap<>();
    private TreeMap<Tuple<XType,YType>,Long> toRemoveHeads = new TreeMap<>();

    public EdgeTypePool(JointDistribution<XType, YType> distribution, long numEdges, long seed) {

        ArrayList<Tuple<XType,YType>> tempCollection = new ArrayList<>();
        for(JointDistribution.Entry<XType,YType> entry : distribution.getEntries()) {
            long numToInsert = (long)(entry.getProbability()*numEdges);
            for(long i = 0; i < numToInsert; i+=1) {
                tempCollection.add(new Tuple<>(entry.getXvalue(), entry.getYvalue()));
            }
        }
        Random random = new Random();
        random.setSeed(seed);
        Collections.shuffle(tempCollection,random);
        for(Tuple<XType,YType> entry : tempCollection) {
            tails.put(entry.getX(),entry.getY());
            heads.put(entry.getY(),entry.getX());
        }
    }

    /**
     * Picks a random edge from the pool
     * @return A randomly choosen edge. null if no remaining edges.
     */
    public Tuple<XType,YType> pickRandomEdge() {
        while(tails.entries().size() > 0) {
            Map.Entry<XType, YType> entry = tails.entries().remove(0);
            Tuple<XType, YType> tuple = new Tuple<>(entry.getKey(), entry.getValue());
            Long num = null;
            num = toRemoveTails.get(tuple);
            if(num == null || num == 0L) {
                num = toRemoveHeads.get(tuple);
                if(num == null) {
                    num = 0L;
                }
                toRemoveHeads.put(tuple,num+1);
                return tuple;
            } else {
                toRemoveTails.put(tuple,num-1);
            }
        }
        return null;
    };

    /**
     * Picks a random edge from the pool whose x value is the given one
     * @param tail The given tail value
     * @return A random edge whose x value is the given one. null if such edge does not exist.
     */
    public Tuple<XType,YType> pickRandomEdgeTail(XType tail) {
        List<YType> collection = tails.get(tail);
        if(collection != null) {
           while(collection.size() > 0) {
               YType next = collection.remove(0);
               Tuple<XType,YType> tuple = new Tuple<XType, YType>(tail,next);
               Long num = 0L;
               if((num = toRemoveTails.get(tuple)) != null) {
                   if(num == 0) {
                       num = toRemoveHeads.get(tuple);
                       if(num == null) {
                           num = 0L;
                       }
                       toRemoveHeads.put(tuple,num+1);
                       return tuple;
                   }
                   toRemoveTails.put(tuple,num-1);
               }
           }
        }
        return null;
    };

    /**
     * Picks a random edge from the pool whose y value is the given one
     * @param head The given head value
     * @return A random edge whose y value is the given one. null if such edge does not exist.
     */

    public Tuple<XType,YType> pickRandomEdgeHead(YType head) {
        List<XType> collection = heads.get(head);
        if(collection != null) {
            while(collection.size() > 0) {
                XType next = collection.remove(0);
                Tuple<XType,YType> tuple = new Tuple<XType, YType>(next,head);
                Long num = 0L;
                if((num = toRemoveHeads.get(tuple)) != null) {
                    if(num == 0) {
                        num = toRemoveTails.get(tuple);
                        if(num == null) {
                            num = 0L;
                        }
                        toRemoveTails.put(tuple,num+1);
                        return tuple;
                    }
                    toRemoveHeads.put(tuple,num-1);
                }
            }
        }
        return null;
    };

    /**
     * Removes a random edge from the pool whose x and y values are the given ones
     * @param tail The given tail value
     * @param head The given head value
     */
    public void removeEdge(XType tail, YType head) {
        Tuple<XType,YType> tuple = new Tuple<XType, YType>(tail,head);
        Long num = 0L;
        if((num = toRemoveTails.get(tuple)) == null) {
            num = 0L;
        }
        toRemoveTails.put(tuple,num+1);

        if((num = toRemoveHeads.get(tuple)) == null) {
            num = 0L;
        }
        toRemoveHeads.put(tuple,num+1);
    };
}
