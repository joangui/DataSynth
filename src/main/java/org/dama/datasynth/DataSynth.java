package org.dama.datasynth;

import static javafx.application.Platform.exit;

import org.dama.datasynth.exec.BuildExecutionPlanException;
import org.dama.datasynth.exec.ExecutionPlan;
import org.dama.datasynth.lang.Parser;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.SemanticException;
import org.dama.datasynth.lang.SyntacticException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by aprat on 10/04/16.
 */
public class DataSynth {
    public static void main( String [] args ) {

        if(args.length != 1) {
            System.err.println("Wrong arguments");
            exit();
        }
        Parser parser = new Parser();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(args[0]));
            Ast ast = parser.parse(new String(encoded, "UTF8"));
            ast.doSemanticAnalysis();
            ExecutionPlan execPlan = new ExecutionPlan();
            execPlan.initialize(ast);

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
