package org.dama.datasynth;

//import static javafx.application.Platform.exit;

import org.dama.datasynth.exec.BuildExecutionPlanException;
import org.dama.datasynth.exec.ExecutionPlan;
import org.dama.datasynth.lang.Parser;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.SemanticException;
import org.dama.datasynth.lang.SyntacticException;
import org.dama.datasynth.generators.Sampler;
import org.dama.datasynth.generators.TextFile;
import org.apache.spark.api.java.function.*;
import org.apache.spark.api.java.*;
import scala.Tuple2;
import org.dama.datasynth.generators.GenID;
import java.util.ArrayList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by aprat on 10/04/16.
 */
public class DataSynth {
    public static void main( String [] args ) {

        if(args.length != 3) {
            System.err.println("Wrong arguments");
            //exit();
	    return;
        }
        SparkEnv.initialize();
        Parser parser = new Parser();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(args[0]));
            Ast ast = parser.parse(new String(encoded, "UTF8"));
            ast.doSemanticAnalysis();
            ExecutionPlan execPlan = new ExecutionPlan();
            execPlan.initialize(ast);
            JavaRDD<Integer> rdd = new GenID().generateIds(10);

            /*Class<?> clazz = Class.forName("org.dama.datasynth.generators.Sampler");
            Constructor<?> constructor = clazz.getConstructor(String.class);

            GTask s = constructor.newInstance(args[1]);*/

            Sampler s = new Sampler(args[1]);
            PairFunction<Integer, Integer, String> f =
                                  new PairFunction<Integer, Integer, String>() {
                                      public Tuple2<Integer, String> call(Integer x) {
                                        return s.run(x);
                                      }
                                  };
            Sampler s2 = new Sampler(args[2]);
            PairFunction<Integer, Integer, String> f2 =
                                new PairFunction<Integer, Integer, String>() {
                                    public Tuple2<Integer, String> call(Integer x) {
                                      return s2.run(x);
                                    }
                                };
            Function<String , ArrayList<String>> f3 =
                                new Function<String , ArrayList<String>>() {
                                    public ArrayList<String> call(String s) {
                                        ArrayList<String> w = new ArrayList<String>();
                                        w.add(s);
                                        return w;
                                    }
                                };
            Function2<ArrayList<String>, ArrayList<String>, ArrayList<String> > f4 =
                                new Function2<ArrayList<String>, ArrayList<String>, ArrayList<String> >() {
                                    public ArrayList<String> call(ArrayList<String> l1, ArrayList<String> l2) {
                                      ArrayList<String> result = new ArrayList<String>();
                                      result.addAll(l1);
                                      result.addAll(l2);
                                      return result;
                                    }
                                };
            JavaPairRDD<Integer, String> rdd1 = rdd.mapToPair(f);
            JavaPairRDD<Integer, String> rdd2 = rdd.mapToPair(f2);
            JavaPairRDD<Integer, ArrayList<String> > result = rdd1.union(rdd2).mapValues(f3).reduceByKey(f4);
            result.saveAsTextFile("output.txt");

        } catch(IOException ioe) {
            System.out.println(ioe);
        } catch(SyntacticException se) {
            System.out.println(se);
        } catch(SemanticException se) {
            System.out.println(se);
        } catch(BuildExecutionPlanException bepe) {
            System.out.println(bepe);
        }
    }
}
