package org.dama.datasynth.runtime.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.dama.datasynth.SparkEnv;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.exec.AttributeTask;
import org.dama.datasynth.exec.EdgeTask;
import org.dama.datasynth.exec.EntityTask;
import org.dama.datasynth.runtime.*;
import org.dama.datasynth.utils.Tuple;
import org.dama.datasynth.utils.TupleUtils;
import scala.Tuple2;

import java.util.*;

/**
 * Created by aprat on 17/04/16.
 */
public class SparkExecutionEngine extends ExecutionEngine {

    private Map<String, JavaPairRDD<Long, Tuple>> attributeRDDs;
    private Map<String, Types.DATATYPE>  attributeTypes;


    public SparkExecutionEngine() {
        attributeRDDs = new HashMap<String,JavaPairRDD<Long,Tuple>>();
        attributeTypes = new HashMap<String, Types.DATATYPE>();
    }


    @Override
    public void dumpData(String outputDir) {
        Function2<Tuple,Tuple,Tuple> f = new Function2<Tuple,Tuple,Tuple>(){
            @Override
            public Tuple call(Tuple t1, Tuple t2) {
                return TupleUtils.concatenate(t1,t2);
            }
        };
        for( Map.Entry<String,JavaPairRDD<Long,Tuple>> entry : attributeRDDs.entrySet() ) {
            entry.getValue().coalesce(1).saveAsTextFile(outputDir+"/"+entry.getKey());
        }
    }

    @Override
    public void execute(EntityTask task ) {
        System.out.println("Preparing Task: "+task.getTaskName());
        List<Long> init = new ArrayList<Long>();
        for(long i = 0; i < task.getNumber(); ++i) {
            init.add(i);
        }
        JavaRDD<Long> ids = SparkEnv.sc.parallelize(init);
        PairFunction<Long, Long, Tuple> f = (PairFunction<Long, Long, Tuple>) id -> new Tuple2<Long, Tuple>(id, new Tuple(id));
        JavaPairRDD<Long, Tuple> idss = ids.mapToPair(f);
        attributeRDDs.put(task.getEntity()+"."+"oid", idss);
        attributeTypes.put(task.getEntity()+"."+"oid", Types.DATATYPE.LONG);
    }

    @Override
    public void execute(AttributeTask task ) throws ExecutionException {
        System.out.println("Preparing Task: "+task.getTaskName());
        String generatorName = task.getGenerator();
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

        /** Calling Initialize Method of the Generator **/
        Object [] params = new Object[task.getInitParameters().size()];
        List<Types.DATATYPE> initParameters = new ArrayList<Types.DATATYPE>();

        int index = 0;
        for( String str : task.getInitParameters()) {
            params[index] = str;
            initParameters.add(Types.DATATYPE.STRING);
            index ++;
        }
        MethodSerializable method = new MethodSerializable(generator,"initialize",initParameters,null);
        method.invoke(params);

        /** Executing the Generator **/
        List<Types.DATATYPE> runParameterTypes = new ArrayList<Types.DATATYPE>();
        for(String param : task.getRunParameters()) {
            Types.DATATYPE dataType = attributeTypes.get(task.getEntity()+"."+param);
            if(dataType == null) throw new ExecutionException("DATATYPE cannot be null");
            runParameterTypes.add(dataType);
        }

        JavaPairRDD<Long, Tuple> entityRDD = attributeRDDs.get(task.getEntity()+".oid");
        JavaPairRDD<Long, Tuple> rdd;
        switch(runParameterTypes.size()){
            case 0: {
                Function0Wrapper fw = new Function0Wrapper(generator, "run", runParameterTypes,task.getAttributeType());
                rdd = entityRDD.mapValues(fw);
            }
            break;
            case 1: {
                JavaPairRDD<Long, Tuple> attributeRDD = attributeRDDs.get(task.getEntity() + "." + task.getRunParameters().get(0));
                //JavaPairRDD<Long,Tuple> entityAttributeRDD = unionRDDs(entityRDD, attributeRDD);
                JavaPairRDD<Long,Tuple> entityAttributeRDD = entityRDD.union(attributeRDD).reduceByKey(TupleUtils.join);
                FunctionWrapper fw = new FunctionWrapper(generator, "run", runParameterTypes,task.getAttributeType());
                rdd = entityAttributeRDD.mapValues(fw);
            }
            break;
            case 2: {
                JavaPairRDD<Long, Tuple> attributeRDD0 = attributeRDDs.get(task.getEntity() + "." + task.getRunParameters().get(0));
                JavaPairRDD<Long, Tuple> attributeRDD1 = attributeRDDs.get(task.getEntity() + "." + task.getRunParameters().get(1));
                //JavaPairRDD<Long,Tuple> entityAttributeRDD = unionRDDs(entityRDD, attributeRDD0, attributeRDD1);
                JavaPairRDD<Long,Tuple> entityAttributeRDD = entityRDD.union(attributeRDD0).union(attributeRDD1).reduceByKey(TupleUtils.join);
                Function2Wrapper fw = new Function2Wrapper(generator, "run", runParameterTypes,task.getAttributeType());
                rdd = entityAttributeRDD.mapValues(fw);
            }
            break;
            default:
                throw new ExecutionException("Unsupported number of parameters");

        }
        attributeRDDs.put(task.getTaskName(),rdd);
        attributeTypes.put(task.getTaskName(),task.getAttributeType());
    }

    @Override
    public void execute(EdgeTask task) throws ExecutionException {
    }
    private JavaPairRDD<Long, Tuple> unionRDDs(JavaPairRDD<Long,Tuple>... rdds){
        JavaPairRDD<Long,Tuple> ent1 = this.attributeRDDs.get("" + ".oid");
        JavaPairRDD<Long,Tuple> ent2 = this.attributeRDDs.get("" + ".oid");

        JavaRDD<Tuple2<Long, Tuple>> aux = SparkEnv.sc.emptyRDD();
        JavaPairRDD<Long,Tuple> result = JavaPairRDD.fromJavaRDD(aux);
        for(JavaPairRDD<Long,Tuple> rdd: rdds){
            result.union(rdd);
        }
        result.reduceByKey(TupleUtils.join);
        return result;

    }
    private Tuple2<JavaPairRDD<Long,Tuple>, JavaPairRDD<Long,Tuple>> buildAttributesRDD(EdgeTask task){

        JavaPairRDD<Long,Tuple> ent1 = this.attributeRDDs.get(task.entity1 + ".oid");
        JavaPairRDD<Long,Tuple> ent2 = this.attributeRDDs.get(task.entity2 + ".oid");

        /*
         Using the emptyRDD for unions seems buggy
        JavaRDD<Tuple2<Long, Tuple>> aux = SparkEnv.sc.emptyRDD();
        JavaPairRDD<Long, Tuple> ent1 = JavaPairRDD.fromJavaRDD(aux);
        JavaPairRDD<Long, Tuple> ent2 = JavaPairRDD.fromJavaRDD(aux);*/
        for(String str: task.getAttributesEnt1()){
            ent1.union(this.attributeRDDs.get(str));
        }
        ent1.reduceByKey(TupleUtils.join);

        for(String str: task.getAttributesEnt2()){
            ent2.union(this.attributeRDDs.get(str));
        }
        ent2.reduceByKey(TupleUtils.join);
        return new Tuple2<>(ent1, ent2);
    }
}
