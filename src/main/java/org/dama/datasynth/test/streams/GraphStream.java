package org.dama.datasynth.test.streams;

/**
 * Created by aprat on 27/02/17.
 */
public interface GraphStream {
    public boolean hasNext();
    public Edge nextEdge();
}
