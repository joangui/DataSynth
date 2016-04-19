package org.dama.datasynth.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.io.Serializable;

public class TextFile implements Serializable {
    public String[][] array;
    private int x;
    private int y;
    private String delimiter;
    TextFile(String str){
        this.x = 0;
        this.y = 1;
        this.delimiter = ":";
        this.process();
    }
    TextFile(String str, int xx, int yy, String del){
        this.x = xx;
        this.y = yy;
        this.delimiter = del;
        this.process();
    }
    private void process(String str){
        try{
            Scanner inFile1 = new Scanner(new File(getClass().getResource(str).toURI()));
            StringBuilder sb = new StringBuilder();
            while(inFile1.hasNext()) {
                sb.append(inFile1.nextLine()+"\n");
            }
            String[] aux = sb.toString().split("\n");
            array = new String[aux.length][2];
            for(int i = 0; i < aux.length; ++i) {
                String[] m = aux[i].split(this.delimiter);
                array[i][0] = m[x];
                array[i][1] = m[y];
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

