package org.dama.datasynth.common;

import org.dama.datasynth.generators.Generator;
import org.dama.datasynth.runtime.ExecutionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Created by aprat on 17/04/16.
 */
public class Types {

    public static class Id implements Comparable<Id> {

        private String name;
        private boolean isTemporal;

        public Id(String name, boolean isTemporal) {
            this.name = name;
            this.isTemporal = isTemporal;
        }

        public boolean isTemporal() {
            return isTemporal;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(Id o) {
            return name.compareTo(o.name);
        }
    }

    /**
     * Used to represent the direction of ane dge
     */
    public enum Direction {
        INGOING ("ingoing"),
        OUTGOING ("outgoing");

        private String text = null;

        Direction(String text) {
            this.text = text;
        }

        /**
         * Gets the text of the EdgeType
         * @return The text of the direction
         */
        public String getText() {
            return text;
        }

        /**
         * Gets the corresponding EdgeType value based on a given text
         * @param text The text representing the direction
         * @return The correposnging EdgeType value;
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

    /**
     * Used to represent the type of an edge
     */
    public enum EdgeType {
        UNDIRECTED ("undirected"),
        DIRECTED ("directed");

        private String text = null;

        EdgeType(String text) {
            this.text = text;
        }

        /**
         * Gets the text of the EdgeType
         * @return The text of the direction
         */
        public String getText() {
            return text;
        }

        /**
         * Gets the corresponding EdgeType value based on a given text
         * @param text The text representing the direction
         * @return The correposnging EdgeType value;
         */
        public static EdgeType fromString(String text) {
            if (text != null) {
                for (EdgeType b : EdgeType.values()) {
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
        BOOLEAN(Boolean.class),
        EDGETYPE(EdgeType.class),
        ID(Id.class);

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

    public static boolean compare(Object a, Object b) {
        Types.DataType dataTypeA = Types.DataType.fromObject(a);
        Types.DataType dataTypeB = Types.DataType.fromObject(b);
        if( dataTypeA != dataTypeB) {
            if(dataTypeA == DataType.EDGETYPE && dataTypeB == DataType.STRING)  {
                return ((EdgeType)a).compareTo(EdgeType.fromString((String)b)) == 0;
            }

            if(dataTypeA == DataType.STRING && dataTypeB == DataType.EDGETYPE)  {
                return ((EdgeType)b).compareTo(EdgeType.fromString((String)a)) == 0;
            }
            return false;
        }
        if(dataTypeA == DataType.BOOLEAN)
                return ((Boolean)a).compareTo((Boolean)b) == 0;
        if(dataTypeA == DataType.INTEGER)
            return ((Integer)a).compareTo((Integer)b) == 0;
        if(dataTypeA == DataType.LONG)
            return ((Long)a).compareTo((Long)b) == 0;
        if(dataTypeA == DataType.STRING)
            return ((String)a).compareTo((String)b) == 0;
        if(dataTypeA == DataType.FLOAT)
            return ((Float)a).compareTo((Float)b) == 0;
        if(dataTypeA == DataType.DOUBLE)
            return ((Double)a).compareTo((Double)b) == 0;
        if(dataTypeA == DataType.ID)
            return ((Id)a).compareTo((Id)b) == 0;
        if(dataTypeA == DataType.EDGETYPE)
            return ((EdgeType)a) == ((EdgeType)b);
        return false;
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

    public static Object invoke(Method method, Generator generator, Object ... params) {
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
                return method.invoke(generator,paramList);
            } else {
                return method.invoke(generator, params);
            }
        } catch (IllegalArgumentException e )  {
            throw new ExecutionException("Unexisting method "+method.getName()+" with "+params.length+" parameters in Generator: "+generator.getClass().getName()+". Method has "+method.getParameterCount()+" parameters.");
        } catch(InvocationTargetException iTE) {
            iTE.printStackTrace();
        } catch(IllegalAccessException iAE) {
            iAE.printStackTrace();
        }
        return null;

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
