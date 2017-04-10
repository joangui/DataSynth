package org.dama.datasynth.matching;

import org.dama.datasynth.matching.graphs.RandomTraversal;
import org.dama.datasynth.matching.graphs.StochasticBlockModelPartitioner;
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

        System.out.println("Running Stochastic Block Model based Matching");
        Map<Long,Long> toReturn = new HashMap<>();

        StochasticBlockModel<XType> blockModel = StochasticBlockModel.extractFrom(edges.size()/2, attributes, distribution);
        Graph graph = Graph.fromTable(edges);
        long [] sizes = new long[blockModel.getNumBlocks()];
        for(Map.Entry<XType,Integer> entry : blockModel.getMapping().entrySet()) {
            long size = blockModel.getSize(entry.getKey());
            sizes[entry.getValue()] = size;
        }

        System.out.println("Running partitioner");
        GraphPartitioner partitioner = new StochasticBlockModelPartitioner(graph, RandomTraversal.class, blockModel);

        Index<XType> index = new Index(attributes);

        List<Long> nodesToPlace = new LinkedList<>();

        Partition partition = partitioner.getPartition();
        for(Map.Entry<Integer,Set<Long>> block : partition.entrySet()) {
            Map<XType,Integer> mapping = blockModel.getMapping();
            XType blockValue = null;
            for(Map.Entry<XType,Integer> entry : mapping.entrySet()) { // find the attribute value that corresponds to this partition
                if(entry.getValue() == (int)block.getKey()) {
                   blockValue = entry.getKey();
                   break;
                }
            }

            for( Long node : block.getValue()) {
                Long id = index.poll(blockValue);
                if(id != null) {
                    toReturn.put(node,id);
                } else {
                    nodesToPlace.add(node);
                }
            }
        }

        List<Long> idsToPlace = new LinkedList<>();
        List<XType> values = new ArrayList<>(index.values());
        for( XType value : values) {
            Long id = null;
            while( (id = index.poll(value)) != null) {
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
