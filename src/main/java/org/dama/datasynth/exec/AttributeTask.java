package org.dama.datasynth.exec;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.runtime.ExecutionEngine;
import org.dama.datasynth.runtime.ExecutionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprat on 20/04/16.
 */
public class AttributeTask extends Task  {

    private String entity = null;
    private String attributeName = null;
    private Types.DATATYPE attributeType = null;

    public String getGenerator() {
        return generator;
    }

    private String generator = null;
    private List<String> runParameters = new ArrayList<String>();

    public List<String> getInitParameters() {
        return initParameters;
    }

    private List<String> initParameters = new ArrayList<String>();

    /**
     * Class constructor
     * @param entity The entity this task is generating something for
     * @param attribute The attribute this task is generating
     */
    public AttributeTask(Ast.Entity entity, Ast.Attribute attribute ) {
        this.entity = entity.getName();
        this.attributeName = attribute.getName();
        this.attributeType = attribute.getType();
        this.generator = attribute.getGenerator().getName();
        for( String param : attribute.getGenerator().getRunParameters()) {
            this.runParameters.add(param);
        }

        for( String param : attribute.getGenerator().getInitParameters()) {
            this.initParameters.add(param);
        }
    }

    /**
     * Gets the entity this task is generating something for
     * @return The entity
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Gets the name of the attribute this task is generating something for
     * @return The attribute
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Gets the type of the attribute this task is generating something for
     * @return The attribute
     */
    public Types.DATATYPE getAttributeType() {
        return attributeType;
    }

    /**
     * Gets the output of the task
     * @return The output of the task
     */
    public String getTaskName() {
        return entity+"."+attributeName;
    }

    /**
     * Gets the parameters of the run method of the generator
     * @return The run parameters of the generator
     */
    public List<String> getRunParameters() {
        return runParameters;
    }


    public void accept(ExecutionEngine engine) throws ExecutionException {
        engine.execute(this);
    }

}
