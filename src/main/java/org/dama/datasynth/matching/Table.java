package org.dama.datasynth.matching;

import org.dama.datasynth.matching.graphs.types.Graph;

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

    public static <Type extends Comparable<Type>> Table<Type,Type> extractValueTable(Table<Long,Long> edges, Table<Long,Type> attributes ) {
        Dictionary<Long,Type> dictionary = new Dictionary(attributes);
        Table<Type,Type> res = new Table<Type, Type>();
        for(Tuple<Long,Long> edge : edges) {
            Type value1 = dictionary.get(edge.getX());
            Type value2 = dictionary.get(edge.getY());
            res.add(new Tuple<Type,Type>(value1,value2));
        }
        return res;
    }

    public static  Table<Long,Long> extractEdges(Graph graph) {
        Table<Long,Long> edges = new Table<Long, Long>();
        for(Long node : graph.getNodes()) {
            for(Long neighbor : graph.getNeighbors(node)) {
                edges.add(new Tuple<>(node,neighbor));
            }
        }
        return edges;
    }
}
