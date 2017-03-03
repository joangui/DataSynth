package org.dama.datasynth.streams;

import org.dama.datasynth.graphreader.types.Edge;

/**
 * Created by aprat on 27/02/17.
 */
public interface GraphStream {
    public boolean hasNext();
    public Edge nextEdge();
}
