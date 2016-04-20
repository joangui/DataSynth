package org.dama.datasynth.runtime;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.dama.datasynth.SparkEnv;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.exec.AttributeTask;
import org.dama.datasynth.exec.EntityTask;
import org.dama.datasynth.exec.ExecutionPlan;
import org.dama.datasynth.exec.Task;

import java.lang.reflect.Method;
import java.util.*;

import static org.apache.hadoop.yarn.webapp.hamlet.HamletSpec.LinkType.index;

/**
 * Created by aprat on 17/04/16.
 */
public class SparkExecutionEngine extends ExecutionEngine {

    private Map<String, JavaRDD<Long>> entityRDDs;
    private Map<String, JavaRDD<Object>> attributeRDDs;
    private Map<String, Types.DATATYPE>  attributeTypes;


    public SparkExecutionEngine() {
        entityRDDs = new HashMap<String,JavaRDD<Long>>();
        attributeRDDs = new HashMap<String,JavaRDD<Object>>();
        attributeTypes = new HashMap<String, Types.DATATYPE>();
    }


    @Override
    public void dumpData(String outputDir) {
        for( Map.Entry<String,JavaRDD<Object>> entry : attributeRDDs.entrySet() ) {
            entry.getValue().coalesce(1).saveAsTextFile(outputDir+"/"+entry.getKey());
        }
    }

    @Override
    public void execute(EntityTask task ) {
        List<Long> init = new ArrayList<Long>();
        for(long i = 0; i < task.getNumber(); ++i) {
            init.add(i);
        }
        JavaRDD<Long> ids = SparkEnv.sc.parallelize(init);
        entityRDDs.put(task.getEntity(), ids);
    }

    @Override
    public void execute(AttributeTask task ) throws ExecutionException {

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

        JavaRDD<Long> entityRDD = entityRDDs.get(task.getEntity());
        JavaRDD<Object> rdd;
        switch(runParameterTypes.size()){
            case 0: {
                Function0Wrapper fw = new Function0Wrapper(generator, "run", runParameterTypes,task.getAttributeType());
                rdd = entityRDD.map(fw);
            }
            break;
            case 1: {
                JavaRDD<Object> attributeRDD = attributeRDDs.get(task.getEntity() + "." + task.getRunParameters().get(0));
                JavaPairRDD<Long,Object> entityAttributeRDD = entityRDD.zip(attributeRDD);
                FunctionWrapper fw = new FunctionWrapper(generator, "run", runParameterTypes,task.getAttributeType());
                rdd = entityAttributeRDD.map(fw);
            }
            break;
            default:
                throw new ExecutionException("Unsupported number of parameters");

        }
        attributeRDDs.put(task.getTaskName(),rdd);
        attributeTypes.put(task.getTaskName(),task.getAttributeType());
    }

}
