package org.dama.datasynth.runtime;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.ForeachPartitionFunction;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.dama.datasynth.DataSynthConfig;
import org.dama.datasynth.SparkEnv;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.schnappi.ast.Ast;
import org.dama.datasynth.schnappi.ast.*;
import org.dama.datasynth.runtime.spark.SchnappiFilter;
import org.dama.datasynth.runtime.spark.untyped.Function0Wrapper;
import org.dama.datasynth.runtime.spark.untyped.Function2Wrapper;
import org.dama.datasynth.runtime.spark.untyped.FunctionWrapper;
import org.dama.datasynth.runtime.spark.untyped.UntypedMethod;
import org.dama.datasynth.schnappi.ast.Number;
import org.dama.datasynth.utils.Tuple;
import org.dama.datasynth.utils.TupleUtils;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

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
        String generatorName = ((Atomic)pn.getParam(0)).getValue();
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
            case "Var":
                return table.get(((Atomic) n).getValue());
            case "Number":
                return new Tuple(((Number)n).getObject(),1);
            case "Function":
                return execFunc((Function)n);
            case "StringLiteral":
                return new Tuple(((StringLiteral)n).getValue(),1);
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
            case "sort" : {
                return execSort(n);

            }
            case "mappart": {
                return execMappart(n);
            }
            case "filter" : {
                return execFilter(n);
            }
            default: {
                throw new ExecutionException("Unsupported function "+n.getName());
            }
        }
    }

    private Tuple execMappart(Function function) {
        Atomic pn0 = (Atomic)function.getParameters().getParam(0);
        Atomic pn1 = (Atomic)function.getParameters().getParam(1);
        Tuple rd = table.get(pn1.getValue());
        org.apache.spark.api.java.function.PairFlatMapFunction<Iterator<Tuple2<Long,Tuple>>,Long,Tuple> f =  (PairFlatMapFunction<Iterator<Tuple2<Long,Tuple>>,Long,Tuple>) tuples -> {
            int blockSize = 10000;
            ArrayList<Tuple2<Long,Tuple>> retList = new ArrayList<Tuple2<Long,Tuple>>();
            ArrayList<Tuple2<Long,Tuple>>  currentBlock = new ArrayList<Tuple2<Long,Tuple>>();
            ArrayList<Integer>  neighborCount = new ArrayList<Integer>();
            while(tuples.hasNext()) {
                currentBlock.clear();
                neighborCount.clear();
                while (currentBlock.size() < blockSize && tuples.hasNext()) {
                    currentBlock.add(tuples.next());
                    neighborCount.add(0);
                }
                for(int i = 0; i < currentBlock.size(); ++i) {
                    for(int j = i; j < currentBlock.size() && j - i < 1000; ++j) {
                        if((Long)currentBlock.get(i)._2().get(2) > neighborCount.get(i) &&
                            (Long)currentBlock.get(j)._2().get(2) > neighborCount.get(j)
                                ) {
                            retList.add(new Tuple2<Long,Tuple>(currentBlock.get(i)._1(), new Tuple(currentBlock.get(j)._1)));
                            neighborCount.set(i,neighborCount.get(i)+1);
                            neighborCount.set(j,neighborCount.get(j)+1);
                        }
                    }
                }
            }
            return retList;
        };
        JavaPairRDD<Long, Tuple> rdd = (JavaPairRDD<Long, Tuple>) rd.get(0);
        JavaPairRDD<Long, Tuple> result = rdd.mapPartitionsToPair(f);
        return new Tuple(result,1);
    }

    private static class TupleComparator implements Comparator<Tuple>, Serializable {

        private List<Long> columns = null;

        public TupleComparator(Long ... args) {
            columns = new ArrayList<Long>();
            for(Long arg : args) {
                columns.add(arg);
            }
        }

        @Override
        public int compare(Tuple tuple1, Tuple tuple2) {
            for(int i = 0; i < columns.size(); ++i) {
               if(tuple1.get(columns.get(i).intValue()).hashCode() != tuple2.get(columns.get(i).intValue()).hashCode()) {
                  if ( (Long)tuple1.get(columns.get(i).intValue()) < (Long)(tuple2.get(columns.get(i).intValue()))) {
                      return -1;
                   }
                   return 1;
               }
            }
            return new Long(((Long)tuple1.get(1) - (Long)(tuple2.get(1)))).intValue();
        }
    }




    public Tuple execSort(Function fn) {
        Id id = (Id)fn.getParameters().getParam(0);
        Number column = (Number)fn.getParameters().getParam(1);
        Tuple rd = table.get(id.getValue());
        JavaPairRDD<Long, Tuple> rdd = (JavaPairRDD<Long, Tuple>) table.get(id.getValue()).get(0);
        PairFunction<Tuple2<Long,Tuple>, Tuple, Long> swapLongTuple = (PairFunction<Tuple2<Long, Tuple>, Tuple, Long>) tuple -> new Tuple2 < Tuple, Long > (tuple._2, tuple._1);
        PairFunction<Tuple2<Tuple,Long>, Long, Tuple> swapTupleLong = (PairFunction<Tuple2<Tuple, Long>, Long, Tuple>) tuple -> new Tuple2 < Long, Tuple > (tuple._2, tuple._1);
        JavaPairRDD <Long, Tuple> sortedRdd = rdd.mapToPair(swapLongTuple).sortByKey(new TupleComparator((Long)column.getObject()), true,100).mapToPair(swapTupleLong);
        return new Tuple(sortedRdd,rd.get(1));
    }

    public Tuple execMap(Function fn) {
        Atomic pn0 = (Atomic)fn.getParameters().getParam(0);
        Atomic pn1 = (Atomic)fn.getParameters().getParam(1);
        Tuple rd = table.get(pn1.getValue());
        org.apache.spark.api.java.function.Function f = fetchFunction(pn0.getValue(), (Integer)rd.get(1));
        JavaPairRDD<Long, Tuple> rdd = (JavaPairRDD<Long, Tuple>) rd.get(0);
        return new Tuple(rdd.mapValues(f),1);
    }

    public Tuple execFilter(Function fn) {
        Atomic pn0 = (Atomic)fn.getParameters().getParam(0);
        SchnappiFilter filter = new SchnappiFilter();
        ArrayList<Integer> filtIds = new ArrayList<>();
        for(int i = 1; i < fn.getParameters().params.size(); ++i) filtIds.add(Integer.parseInt(fn.getParameters().getParam(i).toString()));
        Tuple rd = table.get(pn0.getValue());
        String generatorName = "org.dama.datasynth.runtime.spark.SchnappiFilter";
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
        Object [] params = new Object[filtIds.size()];
        for(int index = 0; index < filtIds.size(); ++index) {
            params[index] = filtIds.get(index);
        }
        UntypedMethod method = new UntypedMethod(generator,"initialize");
        method.invoke(params);
        org.apache.spark.api.java.function.Function f = new FunctionWrapper(generator, "run");
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
        int n = Integer.parseInt(((Atomic)pn.getParam(0)).getValue());
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
