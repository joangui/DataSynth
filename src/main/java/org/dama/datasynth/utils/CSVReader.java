package org.dama.datasynth.utils;
import java.io.File;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.io.Serializable;
/**
 * Created by quim on 4/19/16.
 */
public class CSVReader implements Serializable{
    public String[] array;
    private String pdel;
    private String vdel;
    public CSVReader(String str, String ppdel, String vvdel){
        init(str, ppdel, vvdel);
    }
    public CSVReader(String str){
        init(str, ":", ",");
    }
    private void init(String str, String ppdel, String vvdel) {
        this.pdel = ppdel;
        this.vdel = vvdel;
        try{
            Scanner inFile1 = new Scanner(new File(getClass().getResource(str).toURI()));
            StringBuilder sb = new StringBuilder();
            while(inFile1.hasNext()) {
                sb.append(inFile1.nextLine()+"\n");
            }
            this.array = sb.toString().split("\n");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getLine(int i){
        return this.array[i];
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if(this.array.length > 0) sb.append(this.array[0]);
        for(int i = 1; i < this.array.length; ++i){
            sb.append(this.vdel + array[i]);
        }
        return sb.toString();
    }
    public String fetchSubMatrix(int x, int y){
        StringBuilder sb = new StringBuilder();
        if(this.array.length > 0) {
            String[] aux = array[0].split(this.pdel);
            sb.append(aux[x] + this.pdel + aux[y]);
        }
        for(int i = 1; i < array.length; ++i){
            String[] aux = array[i].split(this.pdel);
            sb.append(this.vdel + aux[x] + this.pdel + aux[y]);
        }
        return sb.toString();
    }
}
