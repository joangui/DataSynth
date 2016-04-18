package org.dama.datasynth.runtime;

import org.dama.datasynth.common.CommonException;
import org.dama.datasynth.common.Types;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprat on 17/04/16.
 */
public class MethodSerializable implements Serializable {

    private Generator g;
    private String    functionName;
    private List<Types.DATATYPE> parameters;
    private Method method;

    public MethodSerializable(Generator g, String functionName, List<Types.DATATYPE> parameters) {
        this.g              = g;
        this.functionName   = functionName;
        this.parameters     = parameters;
        try {
            method = Types.GetMethod(g,functionName,parameters);
        } catch(NullPointerException nPE) {
            nPE.printStackTrace();
        } catch(SecurityException sE) {
            sE.printStackTrace();
        } catch(CommonException cE) {
            cE.printStackTrace();
        }
    }

    public Object invoke(Object ... params) {
        try {
            return method.invoke(g, params);
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
            out.writeInt(parameters.size());
            for(Types.DATATYPE dataType : parameters) {
                out.writeUTF(dataType.getText());
            }
        } catch(java.io.IOException iOE) {
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
            method = Types.GetMethod(g,functionName,parameters);
        } catch(java.io.IOException iOE) {
            iOE.printStackTrace();
        } catch(ClassNotFoundException cNFE) {
            cNFE.printStackTrace();
        } catch(NullPointerException nPE) {
            nPE.printStackTrace();
        } catch(SecurityException sE) {
            sE.printStackTrace();
        } catch(CommonException cE) {
            cE.printStackTrace();
        }
    }
}
