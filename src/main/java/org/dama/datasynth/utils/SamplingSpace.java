package org.dama.datasynth.utils;

import java.io.Serializable;

public class SamplingSpace implements Serializable {
    public String[][] array;
    private String pdelimiter;
    private String vdelimiter;
    public SamplingSpace(String str){
        this.pdelimiter = " ";
        this.vdelimiter = ",";
        this.process(str);
    }
    public SamplingSpace(String str, String pdel, String vdel){
        this.pdelimiter = pdel;
        this.vdelimiter = vdel;
        this.process(str);
    }
    private void process(String str){
        String[] s = str.split(this.vdelimiter);
        if(s.length > 0){
            String[] aux = str.split(this.pdelimiter);
            if(aux.length > 0) {
                this.array = new String[s.length][aux.length];
            }
        }
        for(int i = 0; i < s.length; ++i) {
            String[] ss = s[i].split(this.pdelimiter);
            for(int j = 0; j < ss.length; ++j){
                this.array[i][j] = ss[j];
            }
        }
    }
    public Integer size() {return this.array.length;}
}

