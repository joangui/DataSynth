package org.dama.datasynth.generators.edgegenerators;

import org.dama.datasynth.generators.Generator;
import org.dama.datasynth.utils.Tuple;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by aprat on 5/09/16.
 */
public class UndirectedEdgeGenerator extends Generator {

    public void initialize() {
    }

    public Iterable<scala.Tuple2<Long, Tuple>> run(Iterator<Tuple2<Long,Tuple>> tuples) {
        int blockSize = 10000;
        ArrayList<Tuple2<Long, Tuple>> retList = new ArrayList<Tuple2<Long, Tuple>>();
        ArrayList<Tuple2<Long, Tuple>> currentBlock = new ArrayList<Tuple2<Long, Tuple>>();
        ArrayList<Long> neighborCount = new ArrayList<Long>();
        while (tuples.hasNext()) {
            currentBlock.clear();
            neighborCount.clear();
            while (currentBlock.size() < blockSize && tuples.hasNext()) {
                Tuple2<Long,Tuple> tuple = tuples.next();
                currentBlock.add(tuple);
                neighborCount.add(0L);
            }
            for (int i = 0; i < currentBlock.size(); ++i) {
                for (int j = i+1; j < currentBlock.size() && j - i < 1000; ++j) {
                    if ((Long) currentBlock.get(i)._2().get(2) > neighborCount.get(i) &&
                            (Long) currentBlock.get(j)._2().get(2) > neighborCount.get(j)
                            ) {
                        retList.add(new Tuple2<Long, Tuple>(currentBlock.get(i)._1, new Tuple(currentBlock.get(j)._1)));
                        neighborCount.set(i, neighborCount.get(i) + 1);
                        neighborCount.set(j, neighborCount.get(j) + 1);
                    }
                }
            }
        }
        return retList;
    }
}
