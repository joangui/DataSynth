package org.dama.datasynth.generators;

import java.io.File;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.io.Serializable;

public class TextFile implements Serializable {
    public String[] array;
    TextFile(String str){
        try{
            Scanner inFile1 = new Scanner(new File(getClass().getResource(str).toURI()));
            StringBuilder sb = new StringBuilder();
            while(inFile1.hasNext()) {
                sb.append(inFile1.nextLine()+"\n");
            }
            array = sb.toString().split("\n");
        }catch (Exception e) {
            System.out.println(e);
        }
    }
}
