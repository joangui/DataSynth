package org.dama.datasynth.test.matching;

import java.util.*;

/**
 * Created by aprat on 3/03/17.
 */
public class EdgeTypePool<XType extends Comparable<XType>, YType extends Comparable<YType>> {

    public class Entry {

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
    private LinkedList<Entry> entries                   = new LinkedList<Entry>();

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

    public Entry pickRandomEdge() {
        return entries.removeFirst();
    };

    public Entry pickRandomEdgeX(XType xvalue) {
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

    public Entry pickRandomEdgeY(YType yvalue) {
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

    public void removeEdge(XType xvalue, YType yvalue) {
        ListIterator<Entry> iterator = entries.listIterator();
        while(iterator.hasNext()) {
            Entry entry = iterator.next();
            if(entry.xvalue.compareTo(xvalue) == 0&& entry.yvalue.compareTo(yvalue) == 0) {
                iterator.remove();
                return;
            }
        }
    };
}
