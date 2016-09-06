package org.dama.datasynth.lang;

/**
 * Created by aprat on 10/04/16.
 */
public class SemanticException extends RuntimeException {
    public SemanticException(String message) { super("Semantic Exception "+message); }
}
