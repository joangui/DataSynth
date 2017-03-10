package org.dama.datasynth.matching;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Created by aprat on 6/03/17.
 */
public class Table<T extends Comparable<T>,S extends Comparable<S>> extends ArrayList<Tuple<T,S>> {

    public void load(InputStream inputStream, String separator, Function<String, T> xparser, Function<String, S> yparser) {
        try {
            BufferedReader fileReader =  new BufferedReader( new InputStreamReader(inputStream));
            String line = null;
            while( (line = fileReader.readLine()) != null) {
                String fields [] = line.split(separator);
                add( new Tuple<T,S>(xparser.apply(fields[0]), yparser.apply(fields[1])));
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
