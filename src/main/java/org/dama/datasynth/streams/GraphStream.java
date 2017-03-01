package org.dama.datasynth.streams;

/**
 * Created by aprat on 27/02/17.
 */
public interface GraphStream {
    public boolean hasNext();
    public Edge nextEdge();
}
