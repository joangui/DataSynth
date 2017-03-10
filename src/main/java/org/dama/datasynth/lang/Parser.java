package org.dama.datasynth.lang;

import org.dama.datasynth.common.Types;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

/**
 * Parser for the DataSynth queries
 */
public class Parser {


    /**
     * Parses a generator subquery
     * @param jsonGenerator The json object representing the generator
     * @return An Ast.Generator node representing the parsed generator
     * @throws SyntacticException
     */
    private Ast.Generator parseGenerator(Types.DataType attributeType, JSONObject jsonGenerator) throws SyntacticException{
        Ast.Generator generator = null;
        if(jsonGenerator != null) {
            String name = getFieldNoNull(jsonGenerator, "Generator", "name",String.class);
            generator = new Ast.Generator(name,attributeType);
            JSONArray requires = (JSONArray) jsonGenerator.get("requires");
            if(requires == null) throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD,"requires");
            for (Object runParameter : requires) {
                try {
                    String attributeName = (String)runParameter;
                    if(attributeName.indexOf(".") == -1) throw new SyntacticException(SyntacticException.SyntacticExceptionType.ILLFORMED_ATTRIBUTE_NAME,attributeName);
                    generator.addRunParameter(new Ast.Atomic((String) runParameter));
                } catch(ClassCastException e) {
                    throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE, ". Non-string run parameter");
                }
            }

            JSONArray init = (JSONArray) jsonGenerator.get("init");
            if(init == null) throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD, " init in Generator");
            for (Object initParameter : init) {
                if( initParameter instanceof Long) {
                    generator.addInitParameter(new Ast.Atomic(((Long) initParameter)));
                } else if(initParameter instanceof Double) {
                    generator.addInitParameter(new Ast.Atomic(((Double) initParameter)));
                } else if(initParameter instanceof String) {
                    generator.addInitParameter(new Ast.Atomic(((String) initParameter)));
                } else {
                    throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_VALUE_TYPE," when parsing init");
                }
            }
        }
        return generator;
    }

    /**
     * Gets a field from a json object and checks its type
     * @param jsonObject The json object to get the field from
     * @param objectType The string representing the type of the json object
     * @param fieldName The name of the field to get
     * @param type The Class type of the field to get
     * @param <T> The type of the field to get
     * @return An object with the field value of the specified type.
     * @throws SyntacticException if the field is not of the given type.
     */
    private <T>  T getField(JSONObject jsonObject, String objectType, String fieldName, Class<T> type) throws SyntacticException{
        try{
            return type.cast(jsonObject.get(fieldName));
        } catch(ClassCastException e) {
            throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE,objectType+" \""+fieldName+"\" must be of type "+type.getSimpleName());
        }
    }

    /**
     * Gets a field from a json object, checks its type and checks it is not null
     * @param jsonObject The json object to get the field from
     * @param objectType The string representing the type of the json object
     * @param fieldName The name of the field to get
     * @param type The Class type of the field to get
     * @param <T> The type of the field to get
     * @return An object with the field value of the specified type.
     * @throws SyntacticException if the field is not of the given type or does not exist
     */
    private <T> T getFieldNoNull(JSONObject jsonObject, String objectType, String fieldName, Class<T> type) throws SyntacticException {
        T fieldValue = type.cast(getField(jsonObject,objectType,fieldName,type));
        if(fieldValue == null) throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD, objectType+" must have a field \""+fieldName+"\" ");
        return fieldValue;
    }

    /**
     * Parses a query
     * @param str A string representing the query
     * @return An Ast of the query
     */
    public Ast parse( String str ) throws SyntacticException, SemanticException {
        Ast ast = new Ast();
        HashMap<String, Ast.Entity> hm = new HashMap<>();
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new StringReader(str));
            JSONArray entities = (JSONArray)jsonObject.get("entities");
            for( Object obj : entities ) {
                JSONObject  entity= (JSONObject) obj;
                String entityName = getFieldNoNull(entity,"Entity", "name",String.class);
                Long numInstances = getFieldNoNull(entity,"Entity", "number",Long.class);
                Ast.Entity ent = new Ast.Entity(entityName,numInstances);
                hm.put(entityName, ent);
                JSONArray attributes = (JSONArray) entity.get("attributes");
                if(attributes != null) {
                    for (Object objj : attributes) {
                        JSONObject attribute = (JSONObject) objj;
                        String attributeName = getFieldNoNull(attribute,"Attribute","name",String.class);


                        String attributeTypeString = getFieldNoNull(attribute,"attribute","type",String.class);
                        Types.DataType attributeType = Types.DataType.fromString(attributeTypeString);

                        JSONObject generator = (JSONObject) attribute.get("generator");
                        if(generator == null) throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD, "Attribute must have a \"generator\" field");
                        Ast.Generator gen = parseGenerator(attributeType, generator);
                        Ast.Attribute attr = new Ast.Attribute(
                                ent.getName()+"."+attributeName,
                                attributeType,
                                gen
                        );
                        if (attr.getType() == null)
                            throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_ATTRIBUTE_TYPE,((String) attribute.get("type")));

                        ent.addAttribute(attr);
                    }
                }
                ast.addEntity(ent);
            }

            //EDGE PROCESSING
            JSONArray edges = (JSONArray)jsonObject.get("edges");
            if(edges != null) {
                for (Object obj : edges) {
                    JSONObject jsonedge = (JSONObject) obj;
                    String edgeName = getFieldNoNull(jsonedge, "Edge", "name",String.class);
                    String edgeDirection = getFieldNoNull(jsonedge, "Edge", "edgeType",String.class);
                    String edgeSource = getFieldNoNull(jsonedge, "Edge", "source", String.class);
                    String edgeTarget = getFieldNoNull(jsonedge, "Edge", "target", String.class);

                    Types.EdgeType edgeType = Types.EdgeType.fromString(edgeDirection);
                    if(edgeType == null) throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_DIRECTION_TYPE, edgeDirection+" .Edge edgeType must be either \"directed\" or \"undirected\"");

                    Ast.Edge edge = new Ast.Edge(edgeName+"."+edgeSource+"."+edgeTarget, edgeSource, edgeTarget, edgeType);
                    JSONObject sourceCardinality = (JSONObject) jsonedge.get("sourceCardinality");
                    if(sourceCardinality != null) {
                        JSONObject jsonGenerator = (JSONObject)sourceCardinality.get("generator");
                        edge.setSourceCardinalityGenerator(parseGenerator(Types.DataType.LONG, jsonGenerator));
                        Long number = getField(sourceCardinality,"sourceCardinality","number",Long.class);
                        edge.setSourceCardinalityNumber(number);
                    }

                    JSONObject targetCardinality = (JSONObject) jsonedge.get("targetCardinality");
                    if(targetCardinality != null) {
                        JSONObject jsonGenerator = (JSONObject)targetCardinality.get("generator");
                        edge.setTargetCardinalityGenerator(parseGenerator(Types.DataType.LONG, jsonGenerator));
                        Long number = getField(targetCardinality,"targetCardinality","number",Long.class);
                        edge.setTargetCardinalityNumber(number);
                    }

                    if(edge.getEdgeType() == Types.EdgeType.DIRECTED) {
                        if(((edge.getSourceCardinalityGenerator() != null) && edge.getSourceCardinalityNumber() != null) ||
                          ((edge.getTargetCardinalityGenerator() != null) && edge.getTargetCardinalityNumber() != null)) {
                            throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD, ". Either source or target cardinality is missing");
                        }
                    } else if(edge.getEdgeType() == Types.EdgeType.UNDIRECTED) {
                        if(!((edge.getSourceCardinalityGenerator() != null) || edge.getSourceCardinalityNumber() != null) &&
                               ! ((edge.getTargetCardinalityGenerator() != null) || edge.getTargetCardinalityNumber() != null)) {
                            throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD, ". Either source or target cardinality is missing");
                        }
                    }

                    JSONArray correlates = (JSONArray) jsonedge.get("correlates");
                    if(correlates != null) {
                        for(Object correlated : correlates) {
                            try {
                                String attributeName = (String)correlated;
                                if(attributeName.indexOf(".") == -1) throw new SyntacticException(SyntacticException.SyntacticExceptionType.ILLFORMED_ATTRIBUTE_NAME,attributeName);
                                edge.addCorrelates(new Ast.Atomic((String) attributeName));
                            } catch(ClassCastException e) {
                                throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE, ". Non-string run parameter");
                            }
                        }

                    }
                    ast.addEdge(edge);
                }
            }
            ast.doSemanticAnalysis();
        } catch(ParseException pe) {
            throw new SyntacticException(SyntacticException.SyntacticExceptionType.PARSING_ERROR, pe.toString());
        } catch(SemanticException e) {
            throw e;
        } catch(IOException ioe) {
            System.out.println(ioe);
        }
        return ast;
    }
}
