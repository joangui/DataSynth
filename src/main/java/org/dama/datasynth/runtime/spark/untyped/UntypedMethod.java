package org.dama.datasynth.runtime.spark.untyped;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.runtime.ExecutionException;
import org.dama.datasynth.generators.Generator;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Created by aprat on 17/04/16.
 */
public class UntypedMethod implements Serializable {

    public Generator g;
    private String                  functionName;
    private Method                  method;

    public UntypedMethod(Generator g, String functionName) {
        this.g              = g;
        this.functionName   = functionName;
        try {
            method = Types.getUntypedMethod(g,functionName);
        } catch(NullPointerException nPE) {
            nPE.printStackTrace();
        } catch(SecurityException sE) {
            sE.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Object invoke(List<Object> params) {
        return Types.invoke(method,g,params.toArray());
    }

    private void writeObject(java.io.ObjectOutputStream out) {
        try {
            out.writeObject(g);
            out.writeUTF(functionName);
        } catch(java.io.IOException iOE) {
            iOE.printStackTrace();
        }
    }

    private void readObject(java.io.ObjectInputStream in) {
        try {
            g = (Generator)in.readObject();
            functionName = in.readUTF();
            method = Types.getUntypedMethod(g,functionName);
        } catch(java.io.IOException iOE) {
            iOE.printStackTrace();
        } catch(ClassNotFoundException cNFE) {
            cNFE.printStackTrace();
        } catch(NullPointerException nPE) {
            nPE.printStackTrace();
        } catch(SecurityException sE) {
            sE.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
