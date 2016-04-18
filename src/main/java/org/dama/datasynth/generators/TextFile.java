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
    public List<String> array = new ArrayList<String>();

    TextFile(String str){
        try{
            /*Scanner inFile1 = new Scanner(new File(getClass().getResource(str).toURI()));
            StringBuilder sb = new StringBuilder();
            while(inFile1.hasNext()) {
                sb.append(inFile1.nextLine()+"\n");
            }
            array = sb.toString().split("\n");
            */

            InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(str), "UTF-8");
            BufferedReader inFile = new BufferedReader(reader);
            String line;
            while ((line = inFile.readLine()) != null) {
                array.add(line);
            }
            inFile.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
