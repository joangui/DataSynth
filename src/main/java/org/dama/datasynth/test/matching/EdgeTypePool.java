package org.dama.datasynth.test.matching;

import java.util.*;

/**
 * Created by aprat on 3/03/17.
 */
public class EdgeTypePool< XType extends Comparable<XType>,
                           YType extends Comparable<YType>> {

    static public class Entry< XType extends Comparable<XType>,
                               YType extends Comparable<YType>> {

        private XType xvalue;
        private YType yvalue;

        Entry(XType xvalue, YType yvalue) {
            this.xvalue = xvalue;
            this.yvalue = yvalue;
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
    }

    // fields
    private LinkedList<Entry> entries = new LinkedList<Entry>();

    public EdgeTypePool(JointDistribution<XType, YType> distribution, long numEdges, long seed) {

        for(JointDistribution.Entry<XType,YType> entry : distribution.getEntries()) {
            long numToInsert = (long)(entry.getProbability()*numEdges);
            for(long i = 0; i < numToInsert; i+=1) {
                entries.add(new Entry(entry.getXvalue(), entry.getYvalue()));
            }
        }
        Random random = new Random();
        random.setSeed(seed);
        Collections.shuffle(entries,random);
    }

    /**
     * Picks a random edge from the pool
     * @return A randomly choosen edge. null if no remaining edges.
     */
    public Entry pickRandomEdge() {
        return entries.pollFirst();
    };

    /**
     * Picks a random edge from the pool whose x value is the given one
     * @param xvalue The given x value
     * @return A random edge whose x value is the given one. null if such edge does not exist.
     */
    public Entry pickRandomEdgeTail(XType xvalue) {
        ListIterator<Entry> iterator = entries.listIterator();
        while(iterator.hasNext()) {
            Entry entry = iterator.next();
            if(entry.xvalue.compareTo(xvalue) == 0) {
                iterator.remove();
                return entry;
            }
        }
        return null;
    };

    /**
     * Picks a random edge from the pool whose y value is the given one
     * @param yvalue The given y value
     * @return A random edge whose y value is the given one. null if such edge does not exist.
     */
    public Entry pickRandomEdgeHead(YType yvalue) {
        ListIterator<Entry> iterator = entries.listIterator();
        while(iterator.hasNext()) {
            Entry entry = iterator.next();
            if(entry.yvalue.compareTo(yvalue) == 0) {
                iterator.remove();
                return entry;
            }
        }
        return null;
    };

    /**
     * Removes a random edge from the pool whose x and y values are the given ones
     * @param xvalue The given x value
     * @param yvalue The given y value
     */
    public boolean removeEdge(XType xvalue, YType yvalue) {
        ListIterator<Entry> iterator = entries.listIterator();
        while(iterator.hasNext()) {
            Entry entry = iterator.next();
            if(entry.xvalue.compareTo(xvalue) == 0 && entry.yvalue.compareTo(yvalue) == 0) {
                iterator.remove();
                return true;
            }
        }
        return false;
    };
}
