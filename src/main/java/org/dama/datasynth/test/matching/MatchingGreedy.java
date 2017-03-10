package org.dama.datasynth.test.matching;

import org.dama.datasynth.test.graphs.types.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aprat on 2/03/17.
 */
public class MatchingGreedy {


    /**
     * Computes a matching between entities with attributes and nodes of the graph
     * @param graph The table containing the edges of the graph
     * @param attributes The table containing the mapping between entity id and attribute
     * @param distribution The joint probability distribution of observing pairs of entities with attributes connected
     * @param <XType> The attribute Type
     * @return A Map mapping graph nodes to entities
     */
    public static <XType extends Comparable<XType>> Map<Long,Long> run(Table<Long,Long> graph,
                                                                       Table<Long,XType> attributes,
                                                                       JointDistribution<XType,XType> distribution ) {

        EdgeTypePool<XType,XType> edgeTypePool = new EdgeTypePool(distribution, graph.size(), 1234567890L);
        Index<XType> index = new Index<>(attributes);
        HashMap<Long,Long> mapping = new HashMap<>();
        Dictionary<Long,XType> idToAttributes = new Dictionary<Long,XType>(attributes);

        long edgesCounter = 0L;
        long freeEdges = 0L;
        long halfEdges = 0L;
        long nonFreeEdges = 0L;
        for( Tuple<Long,Long> edge : graph) {
            if((edgesCounter % 5000 ) == 0) {
                System.out.println("Processed "+edgesCounter+" edges.\n" +
                        ""+mapping.size()+" nodes matched.\n" +
                        "free edges: "+freeEdges+"\n"+
                        "half edges: "+halfEdges+"\n"+
                        "nonFreeEdges: "+nonFreeEdges);
                freeEdges = 0L;
                halfEdges = 0L;
                nonFreeEdges = 0L;
            }
            Long tail = mapping.get(edge.getX());
            Long head = mapping.get(edge.getY());
            if(tail == null && head == null) {
                Tuple<XType,XType> type = edgeTypePool.pickRandomEdge();
                freeEdges+=1L;
                if(type != null) {
                    Long candidateX = index.poll(type.getX());
                    Long candidateY = index.poll(type.getY());
                    if (candidateX != null && candidateY != null) {
                        mapping.put(edge.getX(), candidateX);
                        mapping.put(edge.getY(), candidateY);
                    }
                }
            } else if(tail != null && head == null) {
                Tuple<XType,XType> type = edgeTypePool.pickRandomEdgeTail(idToAttributes.get(tail));
                halfEdges+=1L;
                if(type != null) {
                    Long candidate = index.poll(type.getY());
                    if(candidate != null) {
                        mapping.put(edge.getY(), candidate);
                    }
                } else {
                    type = edgeTypePool.pickRandomEdgeHead(idToAttributes.get(tail));
                    if( type != null) {
                        Long candidate = index.poll(type.getX());
                        if(candidate != null ) {
                            mapping.put(edge.getY(), candidate);
                        }
                    }
                }
            } else if(tail == null && head != null) {
                Tuple<XType,XType> type = edgeTypePool.pickRandomEdgeHead(idToAttributes.get(head));
                halfEdges+=1L;
                if(type != null) {
                    Long candidate = index.poll(type.getY());
                    if(candidate != null) {
                        mapping.put(edge.getX(), candidate);
                    }
                } else {
                    type = edgeTypePool.pickRandomEdgeTail(idToAttributes.get(tail));
                    if( type != null) {
                        Long candidate = index.poll(type.getX());
                        if(candidate != null) {
                            mapping.put(edge.getX(), candidate);
                        }
                    }
                }
            } else {
                nonFreeEdges+=1L;
                XType attrTail = idToAttributes.get(tail);
                XType attrHead = idToAttributes.get(head);
                if(attrTail.compareTo(attrHead) < 0) {
                    edgeTypePool.removeEdge(attrTail, attrHead);
                } else {
                    edgeTypePool.removeEdge(attrHead, attrTail);
                }
            }
            edgesCounter+=1;
        }
        return mapping;
    }

    public static <XType extends Comparable<XType>,
                   YType extends Comparable<YType>> void  run( ArrayList<Edge> graph,
                                                               ArrayList<XType> tableX,
                                                               ArrayList<YType> tableY,
                                                               JointDistribution<XType,YType> distribution ) {

        /*EdgeTypePool<XType,YType> edgeTypePool = new EdgeTypePool(distribution, graph.size(), 1234567890L);
        Index<XType> indexX = new Index<>(tableX);
        Index<YType> indexY = new Index<>(tableY);

        HashMap<Integer,Integer> mappingX = new HashMap<Integer,Integer>();
        HashMap<Integer,Integer> mappingY = new HashMap<Integer,Integer>();

        for( Edge edge : graph) {
            Integer tail = mappingX.get(edge.tail);
            Integer head = mappingY.get(edge.head);
            if(tail == null && head == null) {
                EdgeTypePool.Entry<XType,YType> type = edgeTypePool.pickRandomEdge();
                Integer candidateX = indexX.poll(type.getX());
                Integer candidateY = indexY.poll(type.getY());
                // TODO: control the case where any of the candidates is null
                mappingX.put(tail, candidateX);
                mappingY.put(head, candidateY);
                continue;
            }

            if(tail != null && head == null) {
                EdgeTypePool.Entry<XType,YType> type = edgeTypePool.pickRandomEdgeTail(tableX.get(tail));
                // TODO: control the case where no such edge type exists
                Integer candidateY = indexY.poll(type.getY());
                // TODO: control the case where any of the candidates is null
                mappingY.put(head, candidateY);
                continue;
            }

            if(tail == null && head != null) {
                EdgeTypePool.Entry<XType,YType> type = edgeTypePool.pickRandomEdgeHead(tableY.get(head));
                // TODO: control the case where no such edge type exists
                Integer candidateX = indexX.poll(type.getX());
                // TODO: control the case where any of the candidates is null
                mappingX.put(head, candidateX);
                continue;
            }
            edgeTypePool.removeEdge(tableX.get(tail), tableY.get(head));
        }
        */
    }
}
