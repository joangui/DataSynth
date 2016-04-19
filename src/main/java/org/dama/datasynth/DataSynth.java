

package org.dama.datasynth;

//import static javafx.application.Platform.exit;

import com.beust.jcommander.JCommander;
import org.dama.datasynth.exec.BuildExecutionPlanException;
import org.dama.datasynth.exec.ExecutionPlan;
import org.dama.datasynth.runtime.ExecutionException;
import org.dama.datasynth.lang.Parser;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.SemanticException;
import org.dama.datasynth.lang.SyntacticException;
import org.dama.datasynth.runtime.SparkExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;


/**
 * Created by aprat on 10/04/16.
 */

public class DataSynth {

    static private DataSynthConfig config;

    public static void main( String [] args ) {

        config = new DataSynthConfig();
        JCommander jcommander = new JCommander(config,args);
        if(config.help) {
            jcommander.usage();
            return;
        }

        SparkEnv.initialize();
        Parser parser = new Parser();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(config.queryFile));

            long start, end = 0;
            System.out.println("Compiling query ...");
            start = System.currentTimeMillis();
            Ast ast = parser.parse(new String(encoded, "UTF8"));
            ast.doSemanticAnalysis();
            end = System.currentTimeMillis();
            System.out.println("    Query compiled in "+(end-start) + " ms");

            System.out.println("Creating execution plan ...");
            start = System.currentTimeMillis();
            ExecutionPlan execPlan = new ExecutionPlan();
            execPlan.initialize(ast);
            end = System.currentTimeMillis();
            System.out.println("    Execution plan created in  "+(end-start) + " ms");

            System.out.println("Executing query ...");
            start = System.currentTimeMillis();
            SparkExecutor executor = new SparkExecutor();
            executor.Execute(execPlan);
            executor.DumpData(config.outputDir);
            end = System.currentTimeMillis();
            System.out.println("    Query executed in  "+(end-start) + " ms");
        } catch(IOException iOE) {
            iOE.printStackTrace();
        } catch(SyntacticException sE) {
            sE.printStackTrace();
        } catch(SemanticException sE) {
            sE.printStackTrace();
        } catch(BuildExecutionPlanException bEPE) {
            bEPE.printStackTrace();
        } catch(ExecutionException eE) {
            eE.printStackTrace();
        }
    }
}
