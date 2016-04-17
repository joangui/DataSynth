package org.dama.datasynth.runtime;

import org.apache.spark.api.java.function.Function;
import org.dama.datasynth.common.Types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprat on 17/04/16.
 */
public class Function0Wrapper implements Function<Long, Object> {

    private Generator               g;
    private String                  functionName;
    private List<Types.DATATYPE>    parameters;
    private Method                  method;

    public Function0Wrapper( Generator g, String functionName, List<Types.DATATYPE> parameters ) {
        this.g              = g;
        this.functionName   = functionName;
        this.parameters     = parameters;
        try {
            //method = g.getClass().getMethod(functionName);
            method = Types.GetMethod(g,functionName,parameters);
        } catch(NullPointerException nPE) {
            System.out.println(nPE);
            nPE.printStackTrace();
        } catch(SecurityException sE) {
            System.out.println(sE);
            sE.printStackTrace();
        }
    }

    public Object call(Long l) {
        try {
            return (Object)method.invoke(g);
        } catch(InvocationTargetException iTE) {
            System.out.println(iTE);
        } catch(IllegalAccessException iAE) {
            System.out.println(iAE);
        }
        return null;
    }
    private void writeObject(java.io.ObjectOutputStream out) {
        try {
            out.writeObject(g);
            out.writeUTF(functionName);
            out.writeInt(parameters.size());
            for(Types.DATATYPE dataType : parameters) {
                out.writeUTF(dataType.getText());
            }
        } catch(java.io.IOException iOE) {
            System.out.println(iOE);
            iOE.printStackTrace();
        }
    }

    private void readObject(java.io.ObjectInputStream in) {
        try {
            g = (Generator)in.readObject();
            functionName = in.readUTF();
            parameters = new ArrayList<Types.DATATYPE>();
            int numParameters = in.readInt();
            for(int i = 0; i < numParameters; ++i) {
                parameters.add(Types.DATATYPE.fromString(in.readUTF()));
            }
            //method = g.getClass().getMethod(functionName);
            method = Types.GetMethod(g,functionName,parameters);
        } catch(java.io.IOException iOE) {
            System.out.println(iOE);
            iOE.printStackTrace();
        } catch(ClassNotFoundException cNFE) {
            System.out.println(cNFE);
            cNFE.printStackTrace();
        } /*catch(NoSuchMethodException nSME) {
            System.out.println(nSME);
        } */catch(NullPointerException nPE) {
            System.out.println(nPE);
            nPE.printStackTrace();
        } catch(SecurityException sE) {
            System.out.println(sE);
            sE.printStackTrace();
        }
    }
}
