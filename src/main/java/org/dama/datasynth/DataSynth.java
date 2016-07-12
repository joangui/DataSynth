

package org.dama.datasynth;

//import static javafx.application.Platform.exit;

import com.beust.jcommander.JCommander;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.dama.datasynth.exec.BuildDependencyGraphException;
import org.dama.datasynth.exec.DependencyGraph;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.Parser;
import org.dama.datasynth.lang.SemanticException;
import org.dama.datasynth.lang.SyntacticException;
import org.dama.datasynth.program.schnappi.Compiler;
import org.dama.datasynth.program.schnappi.SchnappiGeneratorVisitor;
import org.dama.datasynth.program.schnappi.SchnappiLexer;
import org.dama.datasynth.program.schnappi.SchnappiParser;
import org.dama.datasynth.program.schnappi.ast.Node;
import org.dama.datasynth.program.solvers.Loader;
import org.dama.datasynth.program.solvers.Solver;
import org.dama.datasynth.runtime.ExecutionEngine;
import org.dama.datasynth.runtime.ExecutionException;
import org.dama.datasynth.runtime.SchnappiInterpreter;
import org.dama.datasynth.runtime.spark.SparkExecutionEngine;
import org.dama.datasynth.utils.LogFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.logging.*;

/**
 * Created by aprat on 10/04/16.
 */

public class DataSynth {

    static private DataSynthConfig config;

    private static final Logger logger= Logger.getLogger( DataSynth.class.getSimpleName() );

    public static void main( String [] args ) {

        // Configure logger
        Handler handler = null;
        try {
            handler = new FileHandler("DataSynth.log");
            LogFormatter formatter = new LogFormatter();
            handler.setFormatter(formatter);
        } catch(IOException ioE) {
            ioE.printStackTrace();
            System.exit(1);
        }
        Logger.getLogger(DataSynth.class.getSimpleName()).addHandler(handler);
        Logger.getLogger(DataSynth.class.getSimpleName()).setLevel(Level.FINE);
        //

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
            logger.info("Execution start ...");
            start = System.currentTimeMillis();
            Ast ast = parser.parse(new String(encoded, "UTF8"));
            ast.doSemanticAnalysis();
            end = System.currentTimeMillis();
            logger.info(" Query compiled in "+(end-start) + " ms");

            start = System.currentTimeMillis();
            /*ExecutionPlan execPlan = new ExecutionPlan();
            execPlan.initialize(ast);*/
            DependencyGraph graph = new DependencyGraph(ast);
            end = System.currentTimeMillis();
            System.out.println("Printing graph");
            graph.print();
            logger.info(" Execution plan created in  "+(end-start) + " ms");

            SchnappiLexer SchLexer = new SchnappiLexer( new ANTLRFileStream("src/main/resources/solvers/test.spi"));
            CommonTokenStream tokens = new CommonTokenStream( SchLexer );
            SchnappiParser SchParser = new SchnappiParser( tokens );
            SchnappiParser.SolverContext sctx = SchParser.solver();
            SchnappiGeneratorVisitor visitor = new SchnappiGeneratorVisitor();
            Node n = visitor.visitSolver(sctx);
            String printedAst = "\n > " + n.toStringTabbed("");
            //System.out.println(printedAst);
            logger.log(Level.FINE, printedAst);
            /*ArrayList<Solver> solvers = Loader.loadSolvers("src/main/resources/solvers");
            for(Solver s : solvers){
                System.out.println("\n >" + s.instantiate().getRoot().toStringTabbed(""));
            }*/
            Compiler c = new Compiler("src/main/resources/solvers");
            c.synthesizeProgram(graph);
            logger.log(Level.FINE, c.getProgram().print());
            start = System.currentTimeMillis();
            ExecutionEngine executor = new SparkExecutionEngine();
            SchnappiInterpreter schInt = new SchnappiInterpreter();
            schInt.execProgram(c.getProgram().getRoot());
            schInt.check();
            //executor.execute(execPlan);
            executor.dummyExecute();
            executor.dumpData(config.outputDir);
            end = System.currentTimeMillis();
            logger.info(" Query executed in  "+(end-start) + " ms");
            logger.info("Execution finished");

            return;
        } catch(IOException iOE) {
            iOE.printStackTrace();
            System.exit(1);
        } catch(SyntacticException sE) {
            sE.printStackTrace();
            System.exit(1);
        } catch(SemanticException sE) {
            sE.printStackTrace();
            System.exit(1);
        } catch(BuildDependencyGraphException bEPE) {
            bEPE.printStackTrace();
            System.exit(1);
        } catch(ExecutionException eE) {
            eE.printStackTrace();
            System.exit(1);
        }
    }
}
