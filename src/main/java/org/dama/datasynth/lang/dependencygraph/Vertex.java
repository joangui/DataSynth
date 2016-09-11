package org.dama.datasynth.lang.dependencygraph;

import com.oracle.webservices.internal.api.message.PropertySet;
import org.dama.datasynth.common.Types;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by quim on 5/10/16.
 */
public abstract class Vertex {

    private static long nextId = 0;

    public static class PropertyValue {

        private String value;
        private Types.DataType dataType;

        public PropertyValue(Object value) {
            this.value = value.toString();
            this.dataType = Types.DataType.fromObject(value);
        }

        public String getValue() {
            return value;
        }

        public Types.DataType getDataType() {
            return dataType;
        }
    };

    protected Map<String,PropertyValue> properties = null;


    /**
     * Constructor
     */
    public Vertex(){
        properties = new HashMap<String,PropertyValue>();
        properties.put("id",new PropertyValue(nextId));
        nextId++;
    }

    /**
     * Gets the unique id of the vertex
     * @return The unique id of the vertex
     */
     public long getId() {
        return Long.parseLong(properties.get("id").getValue());
    }

    /**
     * Gets the type of the vertex
     * @return A String with the type of the vertex.
     */
    public String getType() {
        return getClass().getSimpleName();
    }

    /**
     * Checks whether the vertex is of the given type
     * @param type The type to check for
     * @return True if the vertex is of the given type. False otherwise.
     */
    public boolean isType(String type) {
        return getType().compareTo(type) == 0;
    }

    public Map<String,PropertyValue> getProperties() {
        return properties;
    }

    @Override
    public String toString(){
        return "[" + this.getId() + ","+getClass().getSimpleName()+"]";
    }

    public abstract void accept(DependencyGraphVisitor visitor);
}
