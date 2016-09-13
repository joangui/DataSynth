package org.dama.datasynth.runtime.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.dama.datasynth.DataSynthConfig;
import org.dama.datasynth.SparkEnv;
import org.dama.datasynth.runtime.ExecutionException;
import org.dama.datasynth.runtime.spark.untyped.*;
import org.dama.datasynth.runtime.spark.untyped.Function0Wrapper;
import org.dama.datasynth.runtime.spark.untyped.Function2Wrapper;
import org.dama.datasynth.runtime.spark.untyped.FunctionWrapper;
import org.dama.datasynth.schnappi.ast.Ast;
import org.dama.datasynth.schnappi.ast.*;
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

    private Map<Id, Table> idTable;
    private Map<Var,ExpressionValue> varTable;
    private DataSynthConfig config;

    public SchnappiInterpreter() {
        SparkEnv.initialize();
        idTable = new TreeMap<Id, Table>();
        varTable = new TreeMap<Var,ExpressionValue>();
    }

    public SchnappiInterpreter(DataSynthConfig config){
        this();
        this.config = config;
    }

    public void execProgram(Ast ast){
        for(Operation child : ast.getOperations()) execOp(child);
    }

    private void execOp(Operation n){
        execAssig((Assign)n);
    }

    private ExpressionValue execInit(Function fn){
        Parameters pn = (Parameters) fn.getParameters();
        String generatorName = ((Atomic)pn.getParam(0)).getValue();
        org.dama.datasynth.generators.Generator generator = null;
        try {
            generator = (org.dama.datasynth.generators.Generator) Class.forName(generatorName).newInstance();
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
            ExpressionValue exprValue = execExpr(parameters.params.get(index));
            if(exprValue.getType().compareTo("Literal") == 0) {
                params[index] = ((Literal)exprValue).getLiteral().getObject();
            } else {
                throw new ExecutionException("Cannot initialize a generator with a non-literal expression");
            }
        }
        UntypedMethod method = new UntypedMethod(generator,"initialize");
        method.invoke(params);
        JavaRDD<Tuple2<Long, Tuple>> aux = SparkEnv.sc.emptyRDD();
        return new Generator(generator);
    }

    private void execAssig(Assign assign) {
        ExpressionValue exprValue = execExpr(assign.getExpression());

        if(assign.getId().getType().compareTo("Id") == 0) {
            idTable.put(((Id)assign.getId()), (Table) exprValue);
            return;
        }

        if(assign.getId().getType().compareTo("Var") == 0) {
            varTable.put(((Var)assign.getId()), exprValue);
            return;
        }
        throw new ExecutionException("Invalid left value in assignment. Cannot assign anything to a literal");
    }

    private ExpressionValue execExpr(Expression n) {
        String type = n.getType();
        switch (type) {
            case "Id":
                return idTable.get(((Id) n));
            case "Var":
                return varTable.get(((Var) n));
            case "Number":
                return new Literal(((Number)n));
            case "Function":
                return execFunc((Function)n);
            case "StringLiteral":
                return new Literal(((StringLiteral)n));
            default:
                throw new ExecutionException("Unsupported type of expression "+n.getType());
        }
    }

    public ExpressionValue execFunc(Function n){
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

    private Table getTable(Atomic atomic) {

        if(atomic.getType().compareTo("Id") == 0) {
            return idTable.get(((Id)atomic));
        }

        if(atomic.getType().compareTo("Var") == 0) {
            ExpressionValue val = varTable.get(((Var)atomic));
            if(val.getType().compareTo("Table") == 0) {
                return ((Table)val);
            }
        }
        throw new ExecutionException("Unexisting RDD with name "+atomic.getValue());
    }

    private Table execMappart(Function function) {
        Atomic pn0 = (Atomic)function.getParameters().getParam(0);
        Atomic pn1 = (Atomic)function.getParameters().getParam(1);

        JavaPairRDD<Long,Tuple> rdd = getTable(pn1).getRdd();

        if(pn0.getType().compareTo("Var") != 0) {
            throw new ExecutionException("First parameter in mappart must be of type variable containing a generator");
        }

        PairFlatMapFunction f = fetchPartFunction((Var)pn0);
        JavaPairRDD<Long, Tuple> result = rdd.mapPartitionsToPair(f);
        return new Table(result,1);
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




    public Table execSort(Function fn) {
        Atomic id = (Atomic)fn.getParameters().getParam(0);
        Number column = (Number)fn.getParameters().getParam(1);
        Table table = getTable(id);
        JavaPairRDD<Long,Tuple> rdd = table.getRdd();
        PairFunction<Tuple2<Long,Tuple>, Tuple, Long> swapLongTuple = (PairFunction<Tuple2<Long, Tuple>, Tuple, Long>) tuple -> new Tuple2 < Tuple, Long > (tuple._2, tuple._1);
        PairFunction<Tuple2<Tuple,Long>, Long, Tuple> swapTupleLong = (PairFunction<Tuple2<Tuple, Long>, Long, Tuple>) tuple -> new Tuple2 < Long, Tuple > (tuple._2, tuple._1);
        JavaPairRDD <Long, Tuple> sortedRdd = rdd.mapToPair(swapLongTuple).sortByKey(new TupleComparator((Long)column.getObject()), true,100).mapToPair(swapTupleLong);
        return new Table(sortedRdd,table.getCardinality());
    }

    public Table execMap(Function fn) {
        Atomic pn0 = (Atomic)fn.getParameters().getParam(0);
        Atomic pn1 = (Atomic)fn.getParameters().getParam(1);
        Table table  = getTable(pn1);
        org.apache.spark.api.java.function.Function f = fetchFunction((Var)pn0, table.getCardinality());
        JavaPairRDD<Long, Tuple> rdd = table.getRdd();
        return new Table(rdd.mapValues(f),1);
    }

    public Table execFilter(Function fn) {
        Atomic pn0 = (Atomic)fn.getParameters().getParam(0);
        SchnappiFilter filter = new SchnappiFilter();
        ArrayList<Integer> filtIds = new ArrayList<>();
        for(int i = 1; i < fn.getParameters().params.size(); ++i) filtIds.add(Integer.parseInt(fn.getParameters().getParam(i).toString()));
        Table table = getTable(pn0);
        String generatorName = "org.dama.datasynth.runtime.spark.SchnappiFilter";
        org.dama.datasynth.generators.Generator generator = null;
        try {
            generator = (org.dama.datasynth.generators.Generator) Class.forName(generatorName).newInstance();
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
        org.apache.spark.api.java.function.Function f = new org.dama.datasynth.runtime.spark.untyped.FunctionWrapper(generator, "run");
        JavaPairRDD<Long, Tuple> rdd = table.getRdd();
        return new Table(rdd.mapValues(f),1);
    }

    public Table execUnion(Function fn){
        JavaRDD<Tuple2<Long, Tuple>> aux = SparkEnv.sc.emptyRDD();
        JavaPairRDD<Long,Tuple> result = JavaPairRDD.fromJavaRDD(aux);
        Parameters pn = fn.getParameters();
        if(pn.params.size() == 0) return new Table(result,0);
        boolean first = true;
        for(Expression p : pn.params){
            ExpressionValue expr =  execExpr(p);
            if(expr == null) throw new ExecutionException("Error during execution. Unable to find RDD "+p.toString()+", it does not exist");
            if(expr.getType().compareTo("Table") != 0) throw new ExecutionException("Union only accepts Tables as parameters");
            if(first) {
                first = false;
                result = ((Table)expr).getRdd();
            } else {
                result = result.union(((Table)expr).getRdd());
            }
        }
        result = result.reduceByKey(TupleUtils.join);
        return new Table(result,pn.params.size()-1);
    }

    public Table execReduce(Function fn) {
        throw new ExecutionException(("Operation REDUCE not implemented"));
    }

    public JavaPairRDD<Long, Tuple> execEqjoin(Function n){
        throw new ExecutionException(("Operation Equijoin not implemented"));
    }

    public Table execGenids(Function fn){
        Parameters pn = fn.getParameters();
        int n = Integer.parseInt(((Atomic)pn.getParam(0)).getValue());
        List<Long> init = new ArrayList<Long>();
        for(long i = 0; i < n; ++i) {
            init.add(i);
        }
        JavaRDD<Long> ids = SparkEnv.sc.parallelize(init);
        PairFunction<Long, Long, Tuple> f = (PairFunction<Long, Long, Tuple>) id -> new Tuple2<Long, Tuple>(id, new Tuple(id));
        JavaPairRDD<Long, Tuple> idss = ids.mapToPair(f);
        return new Table(idss,1);
    }

    private PairFlatMapFunction fetchPartFunction(Var variable){
        Generator generator = (Generator)varTable.get(variable);
        PairFlatMapFunction fpw = new FuncPartWrapper(generator.getGenerator(), "run");
        return fpw;
    }

    private org.apache.spark.api.java.function.Function fetchFunction(Var variable, int numParams) {
        Generator generator = (Generator)varTable.get(variable);
        org.apache.spark.api.java.function.Function fw = null;
        switch(numParams){
            case 0: {
                fw = new Function0Wrapper(generator.getGenerator(), "run");
            }
            break;
            case 1: {
                fw = new FunctionWrapper(generator.getGenerator(), "run");
            }
            break;
            case 2: {
                fw = new Function2Wrapper(generator.getGenerator(), "run");
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

    public void dumpData(){
        for( Map.Entry<Id,Table> entry : idTable.entrySet() ) {
            JavaPairRDD<Long, Tuple> rdd = entry.getValue().getRdd();
            if(!entry.getKey().isTemporal())
                rdd.coalesce(1).saveAsTextFile(this.config.outputDir +"/" + entry.getKey().getValue());
        }
    }

}
