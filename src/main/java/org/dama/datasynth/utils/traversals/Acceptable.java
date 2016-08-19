package org.dama.datasynth.utils.traversals;

import java.util.List;

/**
 * Created by aprat on 19/08/16.
 */
public interface Acceptable<V> {

    public void accept(Visitor visitor);

    public List<V> neighbors();

}
