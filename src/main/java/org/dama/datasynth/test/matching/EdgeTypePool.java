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
        Map.Entry<XType,YType> entry = tails.entries().remove(0);
        if(entry != null) {
            heads.remove(entry.getValue(),entry.getKey());
            return new Tuple<>(entry.getKey(),entry.getValue());
        }
        return null;
    };

    /**
     * Picks a random edge from the pool whose x value is the given one
     * @param tail The given tail value
     * @return A random edge whose x value is the given one. null if such edge does not exist.
     */
    public Tuple<XType,YType> pickRandomEdgeTail(XType tail) {
        List<YType> entry = tails.get(tail);
        if(entry != null) {
            if(entry.size() > 0) {
                YType head = entry.remove(0);
                heads.remove(head,tail);
                return new Tuple<>(tail, head);
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
            XType tail = collection.remove(collection.size()-1);
            if(tail != null) {
                tails.remove(tail,head);
                return new Tuple<>(tail, head);
            }
        }
        return null;
    };

    /**
     * Removes a random edge from the pool whose x and y values are the given ones
     * @param tail The given tail value
     * @param head The given head value
     */
    public boolean removeEdge(XType tail, YType head) {
        return tails.remove(tail,head) && heads.remove(head,tail);
    };
}
