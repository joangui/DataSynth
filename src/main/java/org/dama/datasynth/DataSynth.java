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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by aprat on 10/04/16.
 */
public class DataSynth {
    public static void main( String [] args ) {

        if(args.length != 2) {
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
            Sampler s = new Sampler(args[1]);
            JavaRDD<Integer> rdd = new GenID().generateIds(10);
            PairFunction<Integer, Integer, String> f =
                                  new PairFunction<Integer, Integer, String>() {
                                      public Tuple2<Integer, String> call(Integer x) {
                                        return s.run(x);
                                      }
                                  };
            rdd.mapToPair(f).saveAsTextFile("output.txt");
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
