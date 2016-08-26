

package org.dama.datasynth;

//import static javafx.application.Platform.exit;

import com.beust.jcommander.JCommander;
import org.dama.datasynth.lang.dependencygraph.DependencyGraph;
import org.dama.datasynth.lang.dependencygraph.TextDependencyGraphPrinter;
import org.dama.datasynth.lang.dependencygraph.Vertex;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.Parser;
import org.dama.datasynth.lang.SemanticException;
import org.dama.datasynth.lang.SyntacticException;
import org.dama.datasynth.program.schnappi.Compiler;
import org.dama.datasynth.program.schnappi.printer.AstTreePrinter;
import org.dama.datasynth.program.schnappi.ast.Operation;
import org.dama.datasynth.runtime.SchnappiInterpreter;
import org.dama.datasynth.utils.LogFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
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
            DependencyGraph graph = new DependencyGraph(ast);

            TextDependencyGraphPrinter printer = new TextDependencyGraphPrinter(graph);
            List<Vertex> roots = graph.getEntities();
            for(Vertex vertex : roots) {
                vertex.accept(printer);
            }

            /*SchnappiLexer SchLexer = new SchnappiLexer( new ANTLRFileStream("src/main/resources/solvers/entitySolver.spi"));
            CommonTokenStream tokens = new CommonTokenStream( SchLexer );
            SchnappiParser SchParser = new SchnappiParser( tokens );
            SchnappiParser.SolverContext sctx = SchParser.solver();
            SchnappiGeneratorVisitor visitor = new SchnappiGeneratorVisitor();
            Node n = visitor.visitSolver(sctx);
            String printedAst = "\n > " + n.toStringTabbed("");
            logger.log(Level.FINE, printedAst);*/

            /*ArrayList<Solver> solvers = Loader.loadSolvers("src/main/resources/solvers");
            for(Solver s : solvers){
                System.out.println("\n >" + s.instantiate().getRoot().toStringTabbed(""));
            }*/

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

            start = System.currentTimeMillis();
            SchnappiInterpreter schInt = new SchnappiInterpreter(config);
            schInt.execProgram(c.getProgram());
            schInt.dumpData();

            /*executor.execute(execPlan);
            executor.dummyExecute();
            executor.dumpData(config.outputDir);*/
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
        }
    }
}
