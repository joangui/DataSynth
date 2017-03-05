package org.dama.datasynth.test.matching;

import org.dama.datasynth.test.graphreader.types.Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by aprat on 2/03/17.
 */
public class Matching {


    public static <XType extends Comparable<XType>> HashMap<Integer,Integer>  run(  ArrayList<Edge> graph,
                                                                                    ArrayList<XType> attributes,
                                                                                    JointDistribution<XType,XType> distribution ) {

        EdgeTypePool<XType,XType> edgeTypePool = new EdgeTypePool(distribution, graph.size(), 1234567890L);
        Index<XType> index = new Index<>(attributes);
        HashMap<Integer,Integer> mapping = new HashMap<Integer,Integer>();

        for( Edge edge : graph) {
            Integer tail = mapping.get(edge.tail);
            Integer head = mapping.get(edge.head);
            if(tail == null && head == null) {
                EdgeTypePool.Entry<XType,XType> type = edgeTypePool.pickRandomEdge();
                Integer candidateX = index.poll(type.getXvalue());
                Integer candidateY = index.poll(type.getYvalue());
                candidateX = candidateX == null ? index.random() : candidateX;
                candidateY = candidateY == null ? index.random() : candidateY;
                mapping.put(tail, candidateX);
                mapping.put(head, candidateY);
                continue;
            }

            if(tail != null && head == null) {
                EdgeTypePool.Entry<XType,XType> type = edgeTypePool.pickRandomEdgeTail(attributes.get(tail));
                if(type != null) {
                    Integer candidate = index.poll(type.getYvalue());
                    candidate = candidate == null ? index.random() : candidate;
                    mapping.put(head, candidate);
                } else {
                    type = edgeTypePool.pickRandomEdgeHead(attributes.get(tail));
                    if( type != null) {
                        Integer candidate = index.poll(type.getXvalue());
                        candidate = candidate == null ? index.random() : candidate;
                        mapping.put(head, candidate);
                    } else {
                        Integer candidate = index.random();
                        mapping.put(head, candidate);
                    }
                }
                continue;
            }

            if(tail != null && head == null) {
                EdgeTypePool.Entry<XType,XType> type = edgeTypePool.pickRandomEdgeHead(attributes.get(head));
                if(type != null) {
                    Integer candidate = index.poll(type.getYvalue());
                    candidate = candidate == null ? index.random() : candidate;
                    mapping.put(tail, candidate);
                } else {
                    type = edgeTypePool.pickRandomEdgeTail(attributes.get(tail));
                    if( type != null) {
                        Integer candidate = index.poll(type.getXvalue());
                        candidate = candidate == null ? index.random() : candidate;
                        mapping.put(tail, candidate);
                    } else {
                        Integer candidate = index.random();
                        mapping.put(tail, candidate);
                    }
                }
                continue;
            }

            if(!edgeTypePool.removeEdge(attributes.get(tail), attributes.get(head))) {
                edgeTypePool.removeEdge(attributes.get(head), attributes.get(tail));
            }
        }
        return mapping;
    }

    public static <XType extends Comparable<XType>,
                   YType extends Comparable<YType>> void  run( ArrayList<Edge> graph,
                                                               ArrayList<XType> tableX,
                                                               ArrayList<YType> tableY,
                                                               JointDistribution<XType,YType> distribution ) {

        EdgeTypePool<XType,YType> edgeTypePool = new EdgeTypePool(distribution, graph.size(), 1234567890L);
        Index<XType> indexX = new Index<>(tableX);
        Index<YType> indexY = new Index<>(tableY);

        HashMap<Integer,Integer> mappingX = new HashMap<Integer,Integer>();
        HashMap<Integer,Integer> mappingY = new HashMap<Integer,Integer>();

        for( Edge edge : graph) {
            Integer tail = mappingX.get(edge.tail);
            Integer head = mappingY.get(edge.head);
            if(tail == null && head == null) {
                EdgeTypePool.Entry<XType,YType> type = edgeTypePool.pickRandomEdge();
                Integer candidateX = indexX.poll(type.getXvalue());
                Integer candidateY = indexY.poll(type.getYvalue());
                // TODO: control the case where any of the candidates is null
                mappingX.put(tail, candidateX);
                mappingY.put(head, candidateY);
                continue;
            }

            if(tail != null && head == null) {
                EdgeTypePool.Entry<XType,YType> type = edgeTypePool.pickRandomEdgeTail(tableX.get(tail));
                // TODO: control the case where no such edge type exists
                Integer candidateY = indexY.poll(type.getYvalue());
                // TODO: control the case where any of the candidates is null
                mappingY.put(head, candidateY);
                continue;
            }

            if(tail == null && head != null) {
                EdgeTypePool.Entry<XType,YType> type = edgeTypePool.pickRandomEdgeHead(tableY.get(head));
                // TODO: control the case where no such edge type exists
                Integer candidateX = indexX.poll(type.getXvalue());
                // TODO: control the case where any of the candidates is null
                mappingX.put(head, candidateX);
                continue;
            }
            edgeTypePool.removeEdge(tableX.get(tail), tableY.get(head));
        }
    }
}
