package org.dama.datasynth.lang;

/**
 * Created by aprat on 10/04/16.
 */
public class SemanticException extends RuntimeException {

    public static enum SemanticExceptionType {
        EDGE_ENDPOINT_NOT_EXISTS ("Missing edge endpoint"),
        GENERATOR_NOT_EXISTS ("Generator does not exist"),
        ATTRIBUTE_NAME_UNEXISTING ("Unexisting attribute name"),
        ATTRIBUTE_NAME_REPEATED ("Repeated attribute name"),
        ATTRIBUTE_NAME_OID ("Invalid attribute name. name oid is reserved");

        private String text = null;
        SemanticExceptionType(String text)  {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private SemanticExceptionType type = null;

    public SemanticException(SemanticExceptionType type, String extraText) {
        super("Semantic Exception "+type.getText()+" "+extraText);
        this.type = type;
    }

    public SemanticExceptionType getType() {
        return type;
    }
}
