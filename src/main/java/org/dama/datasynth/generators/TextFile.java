package org.dama.datasynth.generators;

import java.io.File;
import java.util.Scanner;
import java.lang.StringBuilder;

public class TextFile {
    public String[] array;
    TextFile(String str){
        try{
            Scanner inFile1 = new Scanner(new File(str));
            StringBuilder sb = new StringBuilder();
            while(inFile1.hasNext()) {
                sb.append(inFile1.nextLine());
            }
            array = sb.toString().split("\n");
        }catch (Exception e) {
            System.out.println(e);
        }
    }
}
