package org.dama.datasynth.runtime.spark;

import org.dama.datasynth.runtime.spark.untyped.UntypedMethod;

/**
 * Created by quim on 7/6/16.
 */
public class MethodRef {
    public String id;
    public UntypedMethod method;
    public MethodRef(String iid, UntypedMethod m){
        this.id = iid;
        this.method = m;
    }
}
