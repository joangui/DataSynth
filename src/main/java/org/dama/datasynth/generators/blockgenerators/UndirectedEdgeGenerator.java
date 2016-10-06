package org.dama.datasynth.generators.blockgenerators;

import org.dama.datasynth.generators.Generator;
import org.dama.datasynth.utils.Tuple;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by aprat on 5/09/16.
 */
public class UndirectedEdgeGenerator extends Generator {

    public void initialize() {
    }

    public Iterable<Tuple> run(Iterable<Tuple> tuples) {
        int blockSize = 10000;
        ArrayList<Tuple> retList = new ArrayList<Tuple>();
        ArrayList<Tuple> currentBlock = new ArrayList<Tuple>();
        ArrayList<Long> neighborCount = new ArrayList<Long>();
        Iterator<Tuple> iter = tuples.iterator();
        while (iter.hasNext()) {
            currentBlock.clear();
            neighborCount.clear();
            while (currentBlock.size() < blockSize && iter.hasNext()) {
                Tuple tuple = iter.next();
                currentBlock.add(tuple);
                System.out.println(tuple.toString());
                neighborCount.add(0L);
            }
            for (int i = 0; i < currentBlock.size(); ++i) {
                for (int j = i+1; j < currentBlock.size() && ((Long)currentBlock.get(i).get(2) >= neighborCount.get(i)) && j - i < 1000; ++j) {
                    if ((Long) currentBlock.get(i).get(2) > neighborCount.get(i) &&
                            (Long) currentBlock.get(j).get(2) > neighborCount.get(j)
                            ) {
                        retList.add(new Tuple(currentBlock.get(i).get(0),currentBlock.get(j).get(0)));
                        neighborCount.set(i, neighborCount.get(i) + 1);
                        neighborCount.set(j, neighborCount.get(j) + 1);
                    }
                }
            }
        }
        return retList;
    }
}
