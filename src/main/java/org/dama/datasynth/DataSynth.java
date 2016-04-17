

package org.dama.datasynth;

//import static javafx.application.Platform.exit;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.exec.BuildExecutionPlanException;
import org.dama.datasynth.exec.ExecutionPlan;
import org.dama.datasynth.generators.DummyGenerator;
import org.dama.datasynth.generators.DummyGenerator0;
import org.dama.datasynth.runtime.ExecutionException;
import org.dama.datasynth.runtime.Function0Wrapper;
import org.dama.datasynth.lang.Parser;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.SemanticException;
import org.dama.datasynth.lang.SyntacticException;
import org.dama.datasynth.runtime.FunctionWrapper;
import org.dama.datasynth.runtime.SparkExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprat on 10/04/16.
 */
public class DataSynth {
    public static void main( String [] args ) {

        if(args.length != 1) {
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
            SparkExecutor executor = new SparkExecutor();
            executor.Execute(execPlan);
            executor.DumpData();

            /*List<Long> init = new ArrayList<Long>();
            for(long i = 0; i < 100; ++i) {
                init.add(i);
            }
            JavaRDD<Long> ids = SparkEnv.sc.parallelize(init);

            DummyGenerator0 dummyGenerator0 = new DummyGenerator0();
            dummyGenerator0.initialize("name");
            List<Types.DATATYPE> params0 = new ArrayList<Types.DATATYPE>();
            Function0Wrapper f0w = new Function0Wrapper(dummyGenerator0,"run",params0);
            JavaRDD<Object> countries = ids.map(f0w);
            countries.coalesce(1).saveAsTextFile("./countries.txt");

            DummyGenerator dummyGenerator = new DummyGenerator();
            dummyGenerator.initialize("domain");
            List<Types.DATATYPE> params = new ArrayList<Types.DATATYPE>();
            params.add(Types.DATATYPE.STRING);
            FunctionWrapper fw = new FunctionWrapper(dummyGenerator,"run",params);

            JavaPairRDD<Long,Object> idCountries = ids.zip(countries);
            JavaRDD<Object> emails = idCountries.map(fw);
            emails.coalesce(1).saveAsTextFile("./emails.txt");

            dummyGenerator0.release();
            dummyGenerator.release();
            */
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
