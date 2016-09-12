package org.dama.datasynth.runtime.spark.untyped;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.runtime.ExecutionException;
import org.dama.datasynth.runtime.Generator;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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

    public Object invoke(Object ... params) {
        try {
            Parameter [] parameters = method.getParameters();
            if(parameters.length > 0 && parameters[parameters.length-1].isVarArgs()) {
                Object [] paramList = new Object[parameters.length];
                for(int i = 0; i < method.getParameterCount() -1; ++i) {
                    paramList[i] = params[i];
                }
                Object [] varArgsList = new Object[params.length - parameters.length +1];
                for(int i = parameters.length-1; i < params.length; ++i) {
                    varArgsList[i - parameters.length + 1] = params[i];
                }
                paramList[parameters.length -1] = varArgsList;
                return method.invoke(g,paramList);
            } else {
                return method.invoke(g, params);
            }
        } catch (IllegalArgumentException e )  {
            throw new ExecutionException("Unexisting method "+method.getName()+" with "+params.length+" parameters in Generator: "+g.getClass().getName()+". Method has "+method.getParameterCount()+" parameters.");
        } catch(InvocationTargetException iTE) {
            iTE.printStackTrace();
        } catch(IllegalAccessException iAE) {
            iAE.printStackTrace();
        }
        return null;
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
