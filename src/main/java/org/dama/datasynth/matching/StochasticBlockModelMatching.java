package org.dama.datasynth.matching;

import org.dama.datasynth.matching.graphs.BFSTraversal;
import org.dama.datasynth.matching.graphs.LinearWeightedGreedyPartitioner;
import org.dama.datasynth.matching.graphs.types.Graph;
import org.dama.datasynth.matching.graphs.types.GraphPartitioner;
import org.dama.datasynth.matching.graphs.types.Partition;

import java.util.*;

/**
 * Created by aprat on 10/03/17.
 */
public class StochasticBlockModelMatching implements Matching {

    @Override
    public <XType extends Comparable<XType>> Map<Long, Long> run(Table<Long, Long> edges, Table<Long, XType> attributes, JointDistribution<XType, XType> distribution) {

        Map<Long,Long> toReturn = new HashMap<>();

        StochasticBlockModel<XType> blockModel = StochasticBlockModel.extractFrom(edges.size(), attributes, distribution);
        Graph graph = Graph.fromTable(edges);
        long [] sizes = new long[blockModel.getNumBlocks()];
        long numNodes = 0L;
        for(Map.Entry<XType,Integer> entry : blockModel.getMapping().entrySet()) {
            long size = blockModel.getSize(entry.getKey());
            sizes[entry.getValue()] = size;
            numNodes += size;
        }
        double [] percentages = new double[sizes.length];
        final long numNodesFinal = numNodes;
        Arrays.setAll(percentages, (int i) -> sizes[i]/(double)(numNodesFinal));
        GraphPartitioner partitioner = new LinearWeightedGreedyPartitioner(graph, BFSTraversal.class, percentages);

        Index<XType> index = new Index(attributes);

        List<Long> nodesToPlace = new LinkedList<>();
        List<Long> idsToPlace = new LinkedList<>();

        Partition partition = partitioner.getPartition();
        for(Map.Entry<Integer,Set<Long>> block : partition.entrySet()) {
            Map<XType,Integer> mapping = blockModel.getMapping();
            XType blockValue = null;
            for(Map.Entry<XType,Integer> entry : mapping.entrySet()) {
                if(entry.getValue() == (int)block.getKey()) {
                   blockValue = entry.getKey();
                   break;
                }
            }

            LinkedList<Long> ids = new LinkedList<>(index.getIds(blockValue));
            for( Long node : block.getValue()) {
                if(ids.size() > 0) {
                    toReturn.put(node,ids.pollFirst());
                } else {
                    nodesToPlace.add(node);
                }
            }

            for( Long id : ids) {
               idsToPlace.add(id);
            }
        }

        Collections.shuffle(nodesToPlace);
        Collections.shuffle(idsToPlace);

        Iterator<Long> itNodes = nodesToPlace.iterator();
        Iterator<Long> itIds = idsToPlace.iterator();
        while(itNodes.hasNext() && itIds.hasNext()) {
            toReturn.put(itNodes.next(), itIds.next());
        }
        return toReturn;
    }
}
