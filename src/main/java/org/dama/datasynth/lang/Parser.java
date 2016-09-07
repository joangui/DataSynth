package org.dama.datasynth.lang;

import org.dama.datasynth.common.Types;
import java.io.StringReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Parser {


    private Ast.Generator parseGenerator(JSONObject jsonGenerator) throws SyntacticException{
        Ast.Generator generator = null;
        if(jsonGenerator != null) {
            String name = getFieldNoNull(jsonGenerator, "Generator", "name",String.class);
            generator = new Ast.Generator(name);
            JSONArray runParameters = (JSONArray) jsonGenerator.get("runParameters");
            if(runParameters == null) throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD,"runParameters");
            for (Object runParameter : runParameters) {
                try {
                    String attributeName = (String)runParameter;
                    if(attributeName.indexOf(".") == -1) throw new SyntacticException(SyntacticException.SyntacticExceptionType.ILLFORMED_ATTRIBUTE_NAME,attributeName);
                    generator.addRunParameter(new Ast.Atomic((String) runParameter, Types.DataType.STRING));
                } catch(ClassCastException e) {
                    throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE, ". Non-string run parameter");
                }
            }

            JSONArray initParameters = (JSONArray) jsonGenerator.get("initParameters");
            if(initParameters == null) throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD, " initParameters in Generator");
            for (Object initParameter : initParameters) {
                if( initParameter instanceof Long) {
                    generator.addInitParameter(new Ast.Atomic(((Long) initParameter).toString(), Types.DataType.LONG));
                } else if(initParameter instanceof Double) {
                    generator.addInitParameter(new Ast.Atomic(((Double) initParameter).toString(), Types.DataType.DOUBLE));
                } else if(initParameter instanceof String) {
                    generator.addInitParameter(new Ast.Atomic(((String) initParameter), Types.DataType.STRING));
                } else {
                    throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_VALUE_TYPE," when parsing initParameters");
                }
            }
        }
        return generator;
    }

    private <T>  T getField(JSONObject jsonObject, String objectType, String fieldName, Class<T> type) throws SyntacticException{
        try{
            return type.cast(jsonObject.get(fieldName));
        } catch(ClassCastException e) {
            throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE,objectType+" \""+fieldName+"\" must be of type "+type.getSimpleName());
        }
    }

    private <T> T getFieldNoNull(JSONObject jsonObject, String objectType, String fieldName, Class<T> type) throws SyntacticException {
        T fieldValue = type.cast(getField(jsonObject,objectType,fieldName,type));
        if(fieldValue == null) throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD, objectType+" must have a field \""+fieldName+"\" ");
        return fieldValue;
    }

    /**
     * Parses a data definition schema
     * @param str
     * @return
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
                        Ast.Generator gen = parseGenerator(generator);
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
                    String edgeDirection = getFieldNoNull(jsonedge, "Edge", "direction",String.class);
                    String edgeSource = getFieldNoNull(jsonedge, "Edge", "source", String.class);
                    String edgeTarget = getFieldNoNull(jsonedge, "Edge", "target", String.class);

                    Types.Direction direction = Types.Direction.fromString(edgeDirection);
                    if(direction == null) throw new SyntacticException(SyntacticException.SyntacticExceptionType.INVALID_DIRECTION_TYPE, edgeDirection+" .Edge direction must be either \"directed\" or \"undirected\"");
                    Ast.Edge edge = new Ast.Edge(edgeName, edgeSource, edgeTarget, direction);

                    JSONObject sourceCardinality = (JSONObject) jsonedge.get("sourceCardinality");
                    if(sourceCardinality != null) {
                        JSONObject jsonGenerator = (JSONObject)sourceCardinality.get("generator");
                        edge.setSourceCardinalityGenerator(parseGenerator(jsonGenerator));
                        Long number = getField(sourceCardinality,"sourceCardinality","number",Long.class);
                        edge.setSourceCardinalityNumber(number);
                    }

                    JSONObject targetCardinality = (JSONObject) jsonedge.get("targetCardinality");
                    if(targetCardinality != null) {
                        JSONObject jsonGenerator = (JSONObject)targetCardinality.get("generator");
                        edge.setTargetCardinalityGenerator(parseGenerator(jsonGenerator));
                        Long number = getField(targetCardinality,"targetCardinality","number",Long.class);
                        edge.setTargetCardinalityNumber(number);
                    }

                    if(edge.getDirection() == Types.Direction.DIRECTED) {
                        if(((edge.getSourceCardinalityGenerator() != null) && edge.getSourceCardinalityNumber() != null) ||
                          ((edge.getTargetCardinalityGenerator() != null) && edge.getTargetCardinalityNumber() != null)) {
                            throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD, ". Either source or target cardinality is missing");
                        }
                    } else if(edge.getDirection() == Types.Direction.UNDIRECTED) {
                        if(!((edge.getSourceCardinalityGenerator() != null) || edge.getSourceCardinalityNumber() != null) &&
                               ! ((edge.getTargetCardinalityGenerator() != null) || edge.getTargetCardinalityNumber() != null)) {
                            throw new SyntacticException(SyntacticException.SyntacticExceptionType.MISSING_FIELD, ". Either source or target cardinality is missing");
                        }
                    }

                    JSONObject correllation = (JSONObject) jsonedge.get("correllation");
                    if(correllation != null) {
                        JSONObject jsonGenerator = (JSONObject)correllation.get("generator");
                        edge.setCorrellation(parseGenerator(jsonGenerator));
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
