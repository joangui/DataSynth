package org.dama.datasynth.runtime.spark;

import java.io.Serializable;

/**
 * Created by aprat on 13/09/16.
 */
public class Table<T> extends ExpressionValue implements Serializable {

    private T data = null;

    public Table(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
