package org.dama.datasynth.runtime.spark;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.runtime.Generator;

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
    private String                  functionName;
    private List<Types.DataType>    parameters;
    private Types.DataType returnType;
    private Method                  method;

    public MethodSerializable(Generator g, String functionName, List<Types.DataType> parameters, Types.DataType returnType) {
        this.g              = g;
        this.functionName   = functionName;
        this.parameters     = parameters;
        this.returnType     = returnType;
        try {
            method = Types.getMethod(g,functionName,parameters, returnType);
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
            for(Types.DataType dataType : parameters) {
                out.writeUTF(dataType.getText());
            }
            out.writeUTF(returnType.getText());
        } catch(java.io.IOException iOE) {
            iOE.printStackTrace();
        }
    }

    private void readObject(java.io.ObjectInputStream in) {
        try {
            g = (Generator)in.readObject();
            functionName = in.readUTF();
            parameters = new ArrayList<Types.DataType>();
            int numParameters = in.readInt();
            for(int i = 0; i < numParameters; ++i) {
                parameters.add(Types.DataType.fromString(in.readUTF()));
            }
            returnType = Types.DataType.fromString(in.readUTF());
            method = Types.getMethod(g,functionName,parameters, returnType);
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
