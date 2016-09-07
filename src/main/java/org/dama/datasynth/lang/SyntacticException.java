package org.dama.datasynth.lang;

/**
 * Created by aprat on 10/04/16.
 */
public class SyntacticException extends RuntimeException {

    public static enum SyntacticExceptionType {
        PARSING_ERROR("Parsing error"),
        MISSING_FIELD("Missing field"),
        INVALID_FIELD_TYPE("Invalid field type"),
        INVALID_DIRECTION_TYPE("Invalid Direction type"),
        ILLFORMED_ATTRIBUTE_NAME("Illformed attribute name. Must be of the form <entity>.<name>"),
        INVALID_VALUE_TYPE("Invalid value type"),
        INVALID_ATTRIBUTE_TYPE("Invalid attribute type");
        private String text = null;
        SyntacticExceptionType(String text)  {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    SyntacticExceptionType type = null;

    public SyntacticException(SyntacticExceptionType type, String extraText) {
        super("Syntactic Exception "+type.getText()+" "+extraText);
        this.type = type;
    }

    public SyntacticExceptionType getType() {
        return type;
    }
}
