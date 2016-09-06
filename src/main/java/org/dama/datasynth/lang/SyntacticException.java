package org.dama.datasynth.lang;

/**
 * Created by aprat on 10/04/16.
 */
public class SyntacticException extends RuntimeException {
    public SyntacticException(String message) { super("Syntactic Exception. "+message); }
}
