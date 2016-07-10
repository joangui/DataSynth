package org.dama.datasynth.program.solvers;

/**
 * Created by quim on 7/9/16.
 */
public class SignatureVertex {
    private String type;

    public SignatureVertex(String t){
        this.type = t;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean compareTo(SignatureVertex s){
        return (this.type.compareTo(s.getType()) == 0);
    }
    @Override
    public boolean equals(Object obj){
        SignatureVertex s = (SignatureVertex) obj;
        return this.compareTo(s);
    }
    @Override
    public String toString(){
        return " => " + type;
    }
    @Override
    public int hashCode(){
        int p = 19;
        return p*(type.hashCode());
    }
}
