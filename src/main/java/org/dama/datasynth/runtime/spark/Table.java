package org.dama.datasynth.runtime.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.utils.Tuple;

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
