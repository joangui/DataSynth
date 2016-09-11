

package org.dama.datasynth;


import com.beust.jcommander.JCommander;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.TextDependencyGraphPrinter;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.Parser;
import org.dama.datasynth.lang.SemanticException;
import org.dama.datasynth.lang.SyntacticException;
import org.dama.datasynth.lang.dependencygraph.builder.DependencyGraphBuilder;
import org.dama.datasynth.schnappi.ast.Operation;
import org.dama.datasynth.schnappi.ast.printer.AstTreePrinter;
import org.dama.datasynth.schnappi.Compiler;
import org.dama.datasynth.schnappi.SchnappiGeneratorVisitor;
import org.dama.datasynth.schnappi.SchnappiLexer;
import org.dama.datasynth.schnappi.SchnappiParser;
import org.dama.datasynth.schnappi.ast.printer.AstTreePrinter;
import org.dama.datasynth.schnappi.ast.Operation;
import org.dama.datasynth.schnappi.solver.Solver;
import org.dama.datasynth.utils.LogFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        Parser parser = new Parser();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(config.queryFile));

            long start, end = 0;
            logger.info("Execution start ...");
            start = System.currentTimeMillis();
            Ast ast = parser.parse(new String(encoded, "UTF8"));
            ast.doSemanticAnalysis();
            DependencyGraph graph = DependencyGraphBuilder.buildDependencyGraph(ast);

            TextDependencyGraphPrinter printer = new TextDependencyGraphPrinter(graph);
            printer.print();

            /*
            org.dama.datasynth.schnappi.Compiler c = new org.dama.datasynth.schnappi.Compiler(graph,"src/main/resources/solvers");
            SchnappiLexer SchLexer = new SchnappiLexer( new ANTLRFileStream("src/main/resources/sample.spi"));
            CommonTokenStream tokens = new CommonTokenStream( SchLexer );
            SchnappiParser SchParser = new SchnappiParser( tokens );
            SchnappiParser.SolverContext sctx = SchParser.solver();
            SchnappiGeneratorVisitor visitor = new SchnappiGeneratorVisitor();
            Solver s = visitor.visitSolver(sctx);
            AstTreePrinter astTreePrinter = new AstTreePrinter();
            for(Operation operation : s.ast.getStatements()) {
                operation.accept(astTreePrinter);
            }
            */


            Compiler c = new Compiler(graph,"src/main/resources/solvers");
            start = System.currentTimeMillis();
            c.synthesizeProgram();
            end = System.currentTimeMillis();
            logger.info(" Query compiled in  "+(end-start) + " ms");


            logger.log(Level.FINE,"\nPrinting Schnappi Ast\n");
            AstTreePrinter astTreePrinter = new AstTreePrinter();
            for(Operation operation : c.getProgram().getStatements()) {
                operation.accept(astTreePrinter);
            }

            /*start = System.currentTimeMillis();
            SchnappiInterpreter schInt = new SchnappiInterpreter(config);
            schInt.execProgram(c.getProgram());
            schInt.dumpData();

            end = System.currentTimeMillis();
            logger.info(" Query executed in  "+(end-start) + " ms");
            logger.info("Execution finished");
            */

            /*executor.execute(execPlan);
            executor.dummyExecute();
            executor.dumpData(config.outputDir);*/

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
        }
    }
}
