package org.dama.datasynth.utils;

import java.io.File;
import java.util.Scanner;

/**
 * Created by quim on 5/5/16.
 */
public class Reader {
    public String text;
    public Reader(String str){
        this.text = this.readFile(str);
    }
    public String readFile(String str){
        StringBuilder sb = new StringBuilder();
        try{
            Scanner inFile1 = new Scanner(new File(getClass().getResource(str).toURI()));
            while(inFile1.hasNext()) {
                String line = inFile1.nextLine();
                sb.append(line + "\n");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
