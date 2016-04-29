package org.dama.datasynth.runtime.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import org.dama.datasynth.SparkEnv;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.exec.AttributeTask;
import org.dama.datasynth.exec.EdgeTask;
import org.dama.datasynth.exec.EntityTask;
import org.dama.datasynth.exec.ExecutionPlan;
import org.dama.datasynth.runtime.*;
import scala.Tuple2;

import java.util.*;

/**
 * Created by aprat on 17/04/16.
 */
public class SparkExecutionEngine extends ExecutionEngine {

    private Map<String, JavaPairRDD<Long, Object>> attributeRDDs;
    private Map<String, Types.DATATYPE>  attributeTypes;


    public SparkExecutionEngine() {
        attributeRDDs = new HashMap<String,JavaPairRDD<Long,Object>>();
        attributeTypes = new HashMap<String, Types.DATATYPE>();
    }


    @Override
    public void dumpData(String outputDir) {
        for( Map.Entry<String,JavaPairRDD<Long,Object>> entry : attributeRDDs.entrySet() ) {
            entry.getValue().coalesce(1).saveAsTextFile(outputDir+"/"+entry.getKey());
        }
    }

    @Override
    public void execute(EntityTask task ) {
        System.out.println("Preparing Task: "+task.getTaskName());
        List<Object> init = new ArrayList<Object>();
        for(long i = 0; i < task.getNumber(); ++i) {
            init.add(i);
        }
        JavaRDD<Object> ids = SparkEnv.sc.parallelize(init);
        attributeRDDs.put(task.getEntity()+"."+"oid", ids);
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

        JavaPairRDD<Long, Object> entityRDD = attributeRDDs.get(task.getEntity()+".oid");
        JavaRDD<Object> rdd;
        switch(runParameterTypes.size()){
            case 0: {
                Function0Wrapper fw = new Function0Wrapper(generator, "run", runParameterTypes,task.getAttributeType());
                rdd = entityRDD.map(fw);
            }
            break;
            case 1: {
                JavaPairRDD<Long, Object> attributeRDD = attributeRDDs.get(task.getEntity() + "." + task.getRunParameters().get(0));
                JavaPairRDD<Object,Object> entityAttributeRDD = entityRDD.zip(attributeRDD);
                FunctionWrapper fw = new FunctionWrapper(generator, "run", runParameterTypes,task.getAttributeType());
                rdd = entityAttributeRDD.map(fw);
            }
            break;
            case 2: {
                JavaPairRDD<Long, Object> attributeRDD0 = attributeRDDs.get(task.getEntity() + "." + task.getRunParameters().get(0));
                JavaPairRDD<Long, Object> attributeRDD1 = attributeRDDs.get(task.getEntity() + "." + task.getRunParameters().get(1));
                JavaPairRDD<Object,Object> entityAttributeRDD = entityRDD.zip(attributeRDD0);
                JavaRDD<Tuple2<Object,Object>> a = JavaRDD.fromRDD(JavaPairRDD.toRDD(entityAttributeRDD), entityAttributeRDD.classTag());
                JavaPairRDD<Tuple2<Object,Object>,Object> b = a.zip(attributeRDD1);
                Function2Wrapper fw = new Function2Wrapper(generator, "run", runParameterTypes,task.getAttributeType());
                rdd = b.map(fw);
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

    private JavaRDD<Object> buildAttributesRDD(EdgeTask task){
        JavaRDD<Object> entity1Ids = this.attributeRDDs.get(task.entity1 + ".oid");
        JavaRDD<Object> entity2Ids = this.attributeRDDs.get(task.entity2 + ".oid");

        JavaPairRDD<long, Object> =

        for(String str: attributes){

        }
    }
}
