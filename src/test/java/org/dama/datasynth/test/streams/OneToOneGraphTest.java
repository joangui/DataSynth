package org.dama.datasynth.test.streams;

import org.dama.datasynth.test.graphreader.types.Edge;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 27/02/17.
 */
public class OneToOneGraphTest {

    @Test
    public void TestStream() {
        long numNodes = 100000;
        GraphStream stream = new OneToOneGraph(numNodes);
        ArrayList<Edge> edges = new ArrayList<Edge>();
        while(stream.hasNext()) {
            edges.add(stream.nextEdge());
        }

        long counter = 0;
        for(Edge edge : edges) {
            assertTrue(edge.tail == counter);
            assertTrue(edge.head == (counter+numNodes/2));
            counter++;
        }
        assertTrue(counter == numNodes/2);
    }

}
