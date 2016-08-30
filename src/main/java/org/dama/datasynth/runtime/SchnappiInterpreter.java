package org.dama.datasynth.runtime;

import org.apache.commons.io.FileUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.dama.datasynth.DataSynthConfig;
import org.dama.datasynth.SparkEnv;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.program.Ast;
import org.dama.datasynth.program.schnappi.ast.*;
import org.dama.datasynth.runtime.spark.untyped.Function0Wrapper;
import org.dama.datasynth.runtime.spark.untyped.Function2Wrapper;
import org.dama.datasynth.runtime.spark.untyped.FunctionWrapper;
import org.dama.datasynth.runtime.spark.untyped.UntypedMethod;
import org.dama.datasynth.utils.Tuple;
import org.dama.datasynth.utils.TupleUtils;
import scala.Tuple2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by quim on 6/6/16.
 */
public class SchnappiInterpreter {
    private Map<String, Tuple> table;
    private Map<String, Generator> generators;
    private DataSynthConfig config;

    public SchnappiInterpreter() {
        SparkEnv.initialize();
        table = new HashMap<String, Tuple>();
        generators = new HashMap<String, Generator>();
    }
    public SchnappiInterpreter(DataSynthConfig config){
        this();
        this.config = config;
    }
    public void execProgram(Ast ast){
        for(Operation child : ast.getStatements()) execOp(child);
    }
    public void execOp(Operation n){
        execAssig((Assign)n);
    }

    public Tuple execInit(Function fn){
        Parameters pn = (Parameters) fn.getParameters();
        String generatorName = ((Any)pn.getParam(0)).getValue();
        Generator generator = null;
        try {
            generator = (Generator)Class.forName(generatorName).newInstance();
        } catch (ClassNotFoundException cNFE) {
            cNFE.printStackTrace();
        } catch (InstantiationException iE) {
            iE.printStackTrace();
        } catch (IllegalAccessException iAE) {
            iAE.printStackTrace();
        } finally {
            //System.exit(1);
        }
        Parameters parameters = new Parameters(fn.getParameters());
        parameters.getParams().remove(0);
        Object [] params = new Object[parameters.params.size()];
        for(int index = 0; index < parameters.params.size(); ++index) {
            params[index] = execExpr(parameters.params.get(index)).get(0);
        }
        UntypedMethod method = new UntypedMethod(generator,"initialize");
        method.invoke(params);
        JavaRDD<Tuple2<Long, Tuple>> aux = SparkEnv.sc.emptyRDD();
        return new Tuple(generator,1);
    }

    public void execAssig(Assign assign) {
        Tuple tuple = execExpr(assign.getExpression());
        if(tuple.get(0) instanceof JavaPairRDD) table.put(assign.getId().getValue(), tuple);
        if(tuple.get(0) instanceof Generator) generators.put(assign.getId().getValue(), ((Generator)tuple.get(0)));
    }

    public Tuple execExpr(Expression n) {
        String type = n.getType();
        switch (type) {
            case "Binding":
                return table.get(((Binding) n).getValue());
            case "Id":
                return table.get(((Id) n).getValue());
            case "Any":
                Tuple aux = table.get(((Any) n).getValue());
                if(aux != null) return aux;
                return new Tuple(((Any)n).getValue(),1);
            case "Function":
                return execFunc((Function)n);
            default:
                throw new ExecutionException("Unsupported type of expression "+n.getType());
        }
    }


    public Tuple execFunc(Function n){
        switch(n.getName()){
            case "map" : {
                return execMap(n);
            }
            case "union" : {
                return execUnion(n);
            }
            case "reduce" : {
                return execReduce(n);
            }
            case "genids" :{
                return execGenids(n);
            }
            case "init" : {
                return execInit(n);
            }
            default: {
                return null;
            }
        }
    }
    public Tuple execMap(Function fn) {
        Any pn0 = (Any)fn.getParameters().getParam(0);
        Any pn1 = (Any)fn.getParameters().getParam(1);
        Tuple rd = table.get(pn1.getValue());
        org.apache.spark.api.java.function.Function f = fetchFunction(pn0.getValue(), (Integer)rd.get(1));
        JavaPairRDD<Long, Tuple> rdd = (JavaPairRDD<Long, Tuple>) rd.get(0);
        return new Tuple(rdd.mapValues(f),1);
    }

    public Tuple execUnion(Function fn){
        JavaRDD<Tuple2<Long, Tuple>> aux = SparkEnv.sc.emptyRDD();
        JavaPairRDD<Long,Tuple> result = JavaPairRDD.fromJavaRDD(aux);
        Parameters pn = fn.getParameters();
        if(pn.params.size() == 0) return new Tuple(result,0);
        boolean first = true;
        for(Expression p : pn.params){
            Tuple rdd =  execExpr(p);
            if(rdd == null) throw new ExecutionException("Error during execution. Unable to find RDD "+p.toString()+", it does not exist");
            if(first) {
                first = false;
                result = (JavaPairRDD<Long, Tuple>) rdd.get(0);
            } else {
                result = result.union((JavaPairRDD<Long, Tuple>) rdd.get(0));
            }
        }
        result = result.reduceByKey(TupleUtils.join);
        Tuple tuple = new Tuple();
        tuple.add(result);
        tuple.add(pn.params.size()-1);
        return tuple;
    }

    public Tuple execReduce(Function fn) {
   /*     org.apache.spark.api.java.function.Function f = fetchFunction(fn.getChild(0).id, Integer.parseInt(fn.getChild(1).id));
        JavaRDD<Tuple2<Long, Tuple>> aux = SparkEnv.sc.emptyRDD();
        JavaPairRDD<Long,Tuple> result = JavaPairRDD.fromJavaRDD(aux);
        for(Node an : fn.children){
            Object rdd = execAtom(an);
            result.union((JavaPairRDD<Long,Tuple>)rdd);
        }
        return result;
        */
        throw new ExecutionException(("Operation REDUCE not implemented"));
    }

    public JavaPairRDD<Long, Tuple> execEqjoin(Function n){
        throw new ExecutionException(("Operation Equijoin not implemented"));
    }

    public Tuple execGenids(Function fn){
        Parameters pn = fn.getParameters();
        int n = Integer.parseInt(((Any)pn.getParam(0)).getValue());
        List<Long> init = new ArrayList<Long>();
        for(long i = 0; i < n; ++i) {
            init.add(i);
        }
        JavaRDD<Long> ids = SparkEnv.sc.parallelize(init);
        PairFunction<Long, Long, Tuple> f = (PairFunction<Long, Long, Tuple>) id -> new Tuple2<Long, Tuple>(id, new Tuple(id));
        JavaPairRDD<Long, Tuple> idss = ids.mapToPair(f);
        Tuple tuple = new Tuple();
        tuple.add(idss);
        tuple.add(1);
        return tuple;
    }

    private org.apache.spark.api.java.function.Function fetchFunction(String generatorName, int numParams) {
        Generator generator = this.generators.get(generatorName);
        org.apache.spark.api.java.function.Function fw = null;
        switch(numParams){
            case 0: {
                fw = new Function0Wrapper(generator, "run");
            }
            break;
            case 1: {
                fw = new FunctionWrapper(generator, "run");
            }
            break;
            case 2: {
                fw = new Function2Wrapper(generator, "run");
            }
            break;
            default:
                try {
                    throw new ExecutionException("Unsupported number of parameters");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
        }
        return fw;
    }
    public Object fetchParameter(String param){
        if(table.get(param) != null) return table.get(param);
        else return param;
    }

    public void dumpData(){
        for( Map.Entry<String,Tuple> entry : table.entrySet() ) {
            Object obj = entry.getValue().get(0);
            if(obj instanceof JavaPairRDD) {
                JavaPairRDD<String, Tuple> rdd = (JavaPairRDD<String, Tuple>) obj;
                rdd.coalesce(1).saveAsTextFile(this.config.outputDir +"/" + entry.getKey());
            }
        }
    }

    /*private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }*/
}
