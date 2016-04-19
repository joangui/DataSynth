package org.dama.datasynth.utils;

import java.io.Serializable;

public class SamplingSpace implements Serializable {
    public String[][] array;
    private int x;
    private int y;
    private String pdelimiter;
    private String vdelimiter;
    public SamplingSpace(String str){
        this.x = 0;
        this.y = 1;
        this.pdelimiter = " ";
        this.vdelimiter = ",";
        this.process(str);
    }
    public SamplingSpace(String str, int xx, int yy, String pdel, String vdel){
        this.x = xx;
        this.y = yy;
        this.pdelimiter = pdel;
        this.vdelimiter = vdel;
        this.process(str);
    }
    private void process(String str){
        String[] s = str.split(this.vdelimiter);
        if(s.length > 0){
            String[] aux = str.split(this.pdelimiter);
            if(aux.length > 0) {
                //this.array = new String[s.length][aux.length];
                this.array = new String[s.length][2];
            }
        }
        for(int i = 0; i < s.length; ++i) {
            String[] ss = s[i].split(this.pdelimiter);
            array[i][0] = ss[x];
            array[i][1] = ss[y];
            /*for(int j = 0; j < ss.length; ++j){
                this.array[i][j] = ss[j];
            }*/
        }
    }
    public Integer size() {return this.array.length;}
}

