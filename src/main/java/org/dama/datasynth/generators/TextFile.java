package org.dama.datasynth.generators;

import java.io.File;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.io.Serializable;

public class TextFile implements Serializable {
    public String[][] array;
    TextFile(String str){
        try{
            Scanner inFile1 = new Scanner(new File(getClass().getResource(str).toURI()));
            StringBuilder sb = new StringBuilder();
            while(inFile1.hasNext()) {
                sb.append(inFile1.nextLine()+"\n");
            }
            String[] aux = sb.toString().split("\n");
            array = new String[aux.length][2];
            for(int i = 0; i < aux.length; ++i) {
                String[] m = aux[i].split(":");
                array[i][0] = m[0];
                array[i][1] = m[1];
            }
        }catch (Exception e) {
            System.out.println(e);
        }
    }
}

