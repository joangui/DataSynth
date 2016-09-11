package org.dama.datasynth.schnappi;

/**
 * Created by quim on 6/1/16.
 */
public class CompilerException extends RuntimeException {

    public enum CompilerExceptionType {
        INVALID_BINDING_ASSIGN("Invalid binding in assign operation"),
        UNEXISITING_VERTEX_PROPERTY("Unexisting vertex property"),
        INVALID_BINDING_EXPRESSION("Invalid binding expression"),
        UNSOLVABLE_PROGRAM("Unsolvable program");



        private String text = null;

        CompilerExceptionType(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private CompilerExceptionType type = null;

    public CompilerException(CompilerExceptionType type, String message){
        super(type.getText()+" "+message);
        this.type = type;
    }

    public CompilerExceptionType getType() {
        return type;
    }
}
