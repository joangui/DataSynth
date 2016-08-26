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
    private Map<String, JavaPairRDD<Long, Tuple>> rdds;
    private Map<String, Types.DATATYPE>  attributeTypes;
    private Map<String, Object> table;
    private Map<String, Generator> generators;
    private DataSynthConfig config;

    public SchnappiInterpreter() {
        SparkEnv.initialize();
        rdds = new HashMap<String,JavaPairRDD<Long,Tuple>>();
        attributeTypes = new HashMap<String, Types.DATATYPE>();
        table = new HashMap<String, Object>();
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

    public JavaPairRDD<Long,Tuple> execInit(Function fn){
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
            params[index] = parameters.params.get(index);
        }
        UntypedMethod method = new UntypedMethod(generator,"initialize");
        method.invoke(params);
        this.generators.put(generatorName, generator);
        JavaRDD<Tuple2<Long, Tuple>> aux = SparkEnv.sc.emptyRDD();
        JavaPairRDD<Long,Tuple> result = JavaPairRDD.fromJavaRDD(aux);
        return result;
    }

    public Object execAssig(Assign assign) {
        table.put(assign.getId().getValue(), this.execExpr(assign.getExpression()));
        return null;
    }

    public Object execExpr(Node n){

        if(n.getType().compareTo("Binding") == 0 ){
            return table.get(((Binding)n).getValue());
        }else{
            return execFunc((Function) n);
        }
    }


    public JavaPairRDD<Long, Tuple> execFunc(Function n){
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
    public JavaPairRDD<Long, Tuple> execMap(Function fn) {
        Any pn0 = (Any)fn.getParameters().getParam(0);
        Any pn1 = (Any)fn.getParameters().getParam(1);
        org.apache.spark.api.java.function.Function f = fetchFunction(pn0.getValue(), 1);
        Object rd = fetchRDD(pn1.getValue());
        JavaPairRDD<Long, Tuple> rdd = (JavaPairRDD<Long, Tuple>) rd;
        return rdd.mapValues(f);
    }
    public JavaPairRDD<Long, Tuple> execUnion(Function fn){
        JavaRDD<Tuple2<Long, Tuple>> aux = SparkEnv.sc.emptyRDD();
        JavaPairRDD<Long,Tuple> result = JavaPairRDD.fromJavaRDD(aux);
        Parameters pn = fn.getParameters();
        if(pn.params.size() == 0) return result;
        boolean first = true;
        for(Expression p : pn.params){
            //JavaPairRDD<Long,Tuple> rdd = (JavaPairRDD<Long,Tuple>) execAtom(an);
            JavaPairRDD<Long,Tuple> rdd = (JavaPairRDD<Long,Tuple>) table.get(p);
            if(rdd == null) System.out.println("Null RDD "+p);
            if(first) {
                first = false;
                result = rdd;
            }
            else {
                result = result.union(rdd);
            }
        }
        result = result.reduceByKey(TupleUtils.join);
        return result;
    }

    public JavaPairRDD<Long, Tuple> execReduce(Function fn) {
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
        return null;
    }

    public JavaPairRDD<Long, Tuple> execGenids(Function fn){
        Parameters pn = fn.getParameters();
        int n = Integer.parseInt(((Any)pn.getParam(0)).getValue());
        List<Long> init = new ArrayList<Long>();
        for(long i = 0; i < n; ++i) {
            init.add(i);
        }
        JavaRDD<Long> ids = SparkEnv.sc.parallelize(init);
        PairFunction<Long, Long, Tuple> f = (PairFunction<Long, Long, Tuple>) id -> new Tuple2<Long, Tuple>(id, new Tuple(id));
        JavaPairRDD<Long, Tuple> idss = ids.mapToPair(f);
        return idss;
    }
    private Object fetchRDD(String str){
        //return this.rdds.get(str);
        return this.table.get(str);
    }
    private org.apache.spark.api.java.function.Function fetchFunction(String generatorName, int numParams) {
        Generator generator = null;
        generator = this.generators.get(generatorName);
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
    public void check(){
        for(String str : this.table.keySet()){
            System.out.println(str);
        }
        for(Map.Entry<String, Object> e: this.table.entrySet()){
            System.out.println(e.getKey() + " is empty? " + (e.getValue() == null));
        }
        JavaPairRDD<Long, Tuple> result = (JavaPairRDD<Long, Tuple>) this.table.get("person.final");
        if(result == null) System.out.println("WHY");
        else {
            File f = new File(this.config.outputDir);
            try {
                FileUtils.cleanDirectory(f); //clean out directory (this is optional -- but good know)
                FileUtils.forceDelete(f); //delete directory
            } catch (IOException e) {
                e.printStackTrace();
            }

            result.coalesce(1).saveAsTextFile(this.config.outputDir);
        }
    }
    public void dumpData(){
        for( Map.Entry<String,Object> entry : table.entrySet() ) {
            Object obj = entry.getValue();
            if(obj instanceof JavaPairRDD) {
                JavaPairRDD<String, Tuple> rdd = (JavaPairRDD<String, Tuple>) obj;
                rdd.coalesce(1).saveAsTextFile(this.config.outputDir +"/" + entry.getKey());
            }
        }
    }
    private void deleteDir(String dir){
        File file = new File(dir);
        deleteDir(file);
    }
    private void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
