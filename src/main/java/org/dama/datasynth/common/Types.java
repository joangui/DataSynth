package org.dama.datasynth.common;

import org.dama.datasynth.runtime.Generator;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Created by aprat on 17/04/16.
 */
public class Types {

    /**
     * Used to represent the direction of ane dge
     */
    public enum Direction {
        UNDIRECTED ("undirected"),
        DIRECTED ("directed");

        private String text = null;

        Direction(String text) {
            this.text = text;
        }

        /**
         * Gets the text of the Direction
         * @return The text of the direction
         */
        public String getText() {
            return text;
        }

        /**
         * Gets the corresponding Direction value based on a given text
         * @param text The text representing the direction
         * @return The correposnging Direction value;
         */
        public static Direction fromString(String text) {
            if (text != null) {
                for (Direction b : Direction.values()) {
                    if (text.equalsIgnoreCase(b.getText())) {
                        return b;
                    }
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public enum DataType {
        INTEGER(Integer.class),
        LONG(Long.class),
        STRING(String.class),
        FLOAT(Float.class),
        DOUBLE(Double.class),
        BOOLEAN(Boolean.class);

        private Class typeData;

        DataType(Class typeData) {
            this.typeData = typeData;
        }

        public String getText() {
            return typeData.getSimpleName();
        }

        /**
         * Returns the corresponding DataType to the given string.
         * @param text The string representing the datatype.
         * @return The corresponding DataType.
         */
        public static DataType fromString(String text) {
            if (text != null) {
                for (DataType b : DataType.values()) {
                    if (text.equalsIgnoreCase(b.getText())) {
                        return b;
                    }
                }
            }
            return null;
        }

        public static <T> DataType fromObject(T object)  {
                return fromString(object.getClass().getSimpleName());
        }

    }

    /**
     * Returns an instance of the generator with the given name
     * @param name The full packaged name of the generator to retrieve
     * @return An instance of the generator
     */
    public static Generator getGenerator(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
            return (Generator) Class.forName(name).newInstance();
    }

    /**
     * Given a generator, gets the method with the given name, parameters and return type.
     * @param generator The Generator to retrieve the method from
     * @param methodName The Name of the method to retrieve
     * @param parameterTypes The list of parameter types
     * @param returnType The return type
     * @return The retrieved method.
     * @throws Exception if the method does not exist.
     */
    public static Method getMethod(Generator generator, String methodName, List<DataType> parameterTypes, DataType returnType) throws Exception {
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
        for(DataType param : parameterTypes) {
            paramsString = paramsString+","+param.getText();
        }
        throw new Exception("Generator "+generator.getClass().getName()+" does not have a method with name "+methodName+" with paramters "+parameterTypes.size()+" parameters <"+paramsString+"> and return type "+(returnType != null ? returnType.getText() : "null"));
    }

    /**
     * Given a generator, gets the method with the given name, regardless of the parameters and return type.
     * In case of multiple methods with the same name, returns the first it encounters.
     * @param generator The generator to get the method from
     * @param methodName The name of the method to retrieve
     * @return The retrieved method
     * @throws Exception if the method does not exist.
     */
    public static Method getUntypedMethod(Generator generator, String methodName) throws Exception {
        Method[] methods = generator.getClass().getMethods();
        for(Method m : methods) {
            String mName = m.getName();
            if(mName.compareTo(methodName)==0) {
                return m;
            }
        }
        throw new Exception("Generator "+generator.getClass().getName()+" does not have a method with name "+methodName );
    }
}
