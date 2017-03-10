package org.dama.datasynth.utils;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Created by quim on 4/19/16.
 * Class used to read CSV files
 */
public class CSVReader implements Serializable{
    private String sep;
    private String fileName;
    Scanner scanner;

    /**
     * Constructor
     * @param fileName The csv file name
     * @param sep The separator
     */
    public CSVReader(String fileName, String sep){
        this.sep = sep;
        this.fileName = fileName;
    }

    /**
     * Initializes the reader
     * @param column  The column to retrieve
     */
    public Double[] getDoubleColumn(int column) {
        ArrayList<Double> data = new ArrayList<Double>();
        try{
            scanner = new Scanner(new InputStreamReader(new FileInputStream(fileName)));
            while(scanner.hasNext()) {
                String nextLine = scanner.nextLine();
                String[] columns = nextLine.split(sep);
                data.add(new Double(columns[column]));
            }
            scanner.close();
        }catch (Exception e) {
            System.err.println("Exception when oppening file: "+fileName);
            e.printStackTrace();
        }
        Double [] doublesArray  = new Double[data.size()];
        data.toArray(doublesArray);
        return doublesArray;
    }

    public String[] getStringColumn(int column) {
        ArrayList<String> data = new ArrayList<String>();
        try{
            scanner = new Scanner(new InputStreamReader(new FileInputStream(fileName)));
            while(scanner.hasNext()) {
                String nextLine = scanner.nextLine();
                String[] columns = nextLine.split(sep);
                data.add(columns[column]);
            }
            scanner.close();
        }catch (Exception e) {
            System.err.println("Exception when oppening file: "+fileName);
            e.printStackTrace();
        }
        String [] stringArray  = new String[data.size()];
        data.toArray(stringArray);
        return stringArray;
    }
}
