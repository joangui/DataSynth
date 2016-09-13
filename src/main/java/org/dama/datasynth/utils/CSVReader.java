package org.dama.datasynth.utils;
import java.io.File;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.io.Serializable;
/**
 * Created by quim on 4/19/16.
 * Class used to read CSV files
 */
public class CSVReader implements Serializable{
    public String[] array;
    private String sep;

    /**
     * Constructor
     * @param filename The csv file name
     * @param sep The separator
     */
    public CSVReader(String filename, String sep){
        this.sep = sep;
        init(filename, sep);
    }

    /**
     * Constructor with a default separator of " "
     * @param filename The csv file name.
     */
    public CSVReader(String filename){
        init(filename, " ");
    }

    /**
     * Initializes the reader
     * @param filename The csv file name
     * @param sep The separator
     */
    private void init(String filename, String sep) {
        try{
            Scanner inFile1 = new Scanner(getClass().getResourceAsStream(filename));
            StringBuilder sb = new StringBuilder();
            while(inFile1.hasNext()) {
                String line = inFile1.nextLine();
                sb.append(line + "\n");
            }
            this.array = sb.toString().split("\n");
        }catch (Exception e) {
            System.err.println("Exception when oppening file: "+filename);
            e.printStackTrace();
        }
    }

    /**
     * Gets the ith line of the csv
     * @param i The line to get
     * @return The ith line of the csv
     */
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
