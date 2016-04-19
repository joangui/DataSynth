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
    private String sep;
    public CSVReader(String str, String sep){
        this.sep = sep;
        init(str, sep);
    }
    public CSVReader(String str){
        init(str, " ");
    }
    private void init(String str, String sep) {
        try{
            Scanner inFile1 = new Scanner(new File(getClass().getResource(str).toURI()));
            StringBuilder sb = new StringBuilder();
            while(inFile1.hasNext()) {
                String line = inFile1.nextLine();
                sb.append(line + "\n");
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
        return toString(" ",",");
    }

    public String toString(String pdel, String vdel){
        StringBuilder sb = new StringBuilder();
        if(this.array.length > 0) sb.append(this.array[0]);
        for(int i = 1; i < this.array.length; ++i){
            sb.append(vdel + array[i].replaceAll(sep,pdel));
        }
        return sb.toString();
    }

    public String fetchSubMatrix(int x, int y){
        return fetchSubMatrix(x,y," ",",");
    }

    public String fetchSubMatrix(int x, int y, String pdel, String vdel){
        StringBuilder sb = new StringBuilder();
        if(this.array.length > 0) {
            String[] aux = array[0].split(sep);
            sb.append(aux[x] + pdel + aux[y]);
        }
        for(int i = 1; i < array.length; ++i){
            String[] aux = array[i].split(sep);
            sb.append(vdel + aux[x] + pdel + aux[y]);
        }
        return sb.toString();
    }
}
