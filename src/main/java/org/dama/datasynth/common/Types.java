package org.dama.datasynth.common;

import org.dama.datasynth.runtime.Generator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Created by aprat on 17/04/16.
 */
public class Types {

    public enum DATATYPE {
        INTEGER(Integer.class),
        LONG(Long.class),
        STRING(String.class),
        FLOAT(Float.class),
        DOUBLE(Double.class),
        BOOLEAN(Boolean.class);

        private Class typeData;

        DATATYPE(Class typeData) {
            this.typeData = typeData;
        }

        public String getText() {
            return typeData.getSimpleName();
        }

        /**
         * Returns the corresponding DATATYPE to the given string.
         * @param text The string representing the datatype.
         * @return The corresponding DATATYPE.
         */
        public static DATATYPE fromString(String text) {
            if (text != null) {
                for (DATATYPE b : DATATYPE.values()) {
                    if (text.equalsIgnoreCase(b.getText())) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    public static Generator Generator(String name) {
        try {
            return (Generator) Class.forName(name).newInstance();
        } catch(ClassNotFoundException cNFE) {
            cNFE.printStackTrace();
        } catch(InstantiationException iE) {
            iE.printStackTrace();
        } catch(IllegalAccessException iAE) {
            iAE.printStackTrace();
        }
        return null;
    }

    /**
     * Given a generator, gets the method with the given name, parameters and return type.
     * @param generator The Generator to retrieve the method from
     * @param methodName The Name of the method to retrieve
     * @param parameterTypes The list of parameter types
     * @param returnType The return type
     * @return The retrieved method.
     * @throws CommonException if the method does not exist.
     */
    public static Method GetMethod(Generator generator, String methodName, List<DATATYPE> parameterTypes, DATATYPE returnType) throws CommonException {
        Method[] methods = generator.getClass().getMethods();
        for(Method m : methods) {
            String mName = m.getName();
            if(mName.compareTo(methodName)==0) {
                boolean match = true;
                if(m.getParameters().length != parameterTypes.size())
                    continue;
                int index = 0;
                for(Parameter param : m.getParameters()) {
                    String paramType = param.getType().getSimpleName();
                    if(paramType.compareTo(parameterTypes.get(index).getText()) != 0) {
                        match = false;
                        break;
                    }
                    index++;
                }
                if(match) {
                    if((returnType == null) || (returnType != null && m.getReturnType().getSimpleName().compareTo(returnType.getText()) == 0))
                        return m;
                }
            }
        }
        String paramsString = new String();
        for(DATATYPE param : parameterTypes) {
            paramsString = paramsString+","+param.getText();
        }
        throw new CommonException("Generator "+generator.getClass().getName()+" does not have a method with name "+methodName+" with paramters "+parameterTypes.size()+" parameters <"+paramsString+"> and return type "+(returnType != null ? returnType.getText() : "null"));
    }

    /**
     * Given a generator, gets the method with the given name, regardless of the parameters and return type.
     * In case of multiple methods with the same name, returns the first it encounters.
     * @param generator The generator to get the method from
     * @param methodName The name of the method to retrieve
     * @return The retrieved method
     * @throws CommonException if the method does not exist.
     */
    public static Method GetUntypedMethod(Generator generator, String methodName) throws CommonException {
        Method[] methods = generator.getClass().getMethods();
        for(Method m : methods) {
            String mName = m.getName();
            if(mName.compareTo(methodName)==0) {
                return m;
            }
        }
        throw new CommonException("Generator "+generator.getClass().getName()+" does not have a method with name "+methodName );
    }
}
