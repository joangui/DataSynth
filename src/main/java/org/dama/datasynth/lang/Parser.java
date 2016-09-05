package org.dama.datasynth.lang;

import org.dama.datasynth.common.Types;
import java.io.StringReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Parser {


    private Ast.Generator parseGenerator(JSONObject jsonGenerator) {
        if(jsonGenerator != null) {

        }
        return null;
    }

    /**
     * Parses a data definition schema
     * @param str
     * @return
     */
    public Ast parse( String str ) throws SyntacticException {
        Ast ast = new Ast();
        HashMap<String, Ast.Entity> hm = new HashMap<>();
        HashMap<String, Ast.Attribute> hmAttr = new HashMap<>();
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new StringReader(str));
            JSONArray entities = (JSONArray)jsonObject.get("entities");
            for( Object obj : entities ) {
                JSONObject  entity= (JSONObject) obj;
                String entityName = (String)entity.get("name");
                Long numInstances = (Long)entity.get("number");
                Ast.Entity ent = new Ast.Entity(entityName,numInstances);
                hm.put(entityName, ent);
                JSONArray attributes = (JSONArray) entity.get("attributes");
                if(attributes != null) {
                    for (Object objj : attributes) {
                        JSONObject attribute = (JSONObject) objj;
                        JSONObject generator = (JSONObject) attribute.get("generator");
                        Ast.Generator gen = new Ast.Generator((String) generator.get("name"));
                        JSONArray runParameters = (JSONArray) generator.get("runParameters");
                        for (Object runParameter : runParameters) {
                            gen.addInitParameter( new Ast.Atomic((String)runParameter, Types.DataType.STRING));
                        }

                        JSONArray initParameters = (JSONArray) generator.get("initParameters");
                        for (Object initParameter : initParameters) {
                            if( initParameter instanceof Long) {
                                gen.addInitParameter(new Ast.Atomic(((Long) initParameter).toString(), Types.DataType.LONG));
                            } else if(initParameter instanceof Double) {
                                gen.addInitParameter(new Ast.Atomic(((Double) initParameter).toString(), Types.DataType.DOUBLE));
                            } else if(initParameter instanceof String) {
                                gen.addInitParameter(new Ast.Atomic(((String) initParameter), Types.DataType.STRING));
                            } else {
                                throw new SyntacticException("Error when parsing json. Unrecognizable attribute type at initParamters");
                            }
                        }

                        Ast.Attribute attr = new Ast.Attribute(
                                ent.getName()+"."+attribute.get("name"),
                                Types.DataType.fromString((String) attribute.get("type")),
                                gen
                        );
                        hmAttr.put(ent.getName() + "." + attr.getName(), attr);
                        if (attr.getType() == null)
                            throw new SyntacticException(((String) attribute.get("type")) + " is not a valid data type ");

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
                    String edgeName = (String) jsonedge.get("name");
                    String edgeDirection = (String) jsonedge.get("direction");
                    String edgeSource = (String) jsonedge.get("source");
                    String edgeTarget = (String) jsonedge.get("target");

                    Ast.Edge edge = new Ast.Edge(edgeName, edgeSource, edgeTarget, Types.Direction.fromString(edgeDirection));

                    JSONObject sourceCardinality = (JSONObject) jsonedge.get("sourceCardinality");
                    if(sourceCardinality != null) {
                        JSONObject jsonGenerator = (JSONObject)sourceCardinality.get("generator");
                        edge.setSourceCardinalityGenerator(parseGenerator(jsonGenerator));
                        Long number = (Long)sourceCardinality.get("number");
                        edge.setSourceCardinalityNumber(number);

                    }

                    JSONObject targetCardinality = (JSONObject) jsonedge.get("targetCardinality");
                    if(targetCardinality != null) {
                        JSONObject jsonGenerator = (JSONObject)targetCardinality.get("generator");
                        edge.setTargetCardinalityGenerator(parseGenerator(jsonGenerator));
                        Long number = (Long)targetCardinality.get("number");
                        edge.setTargetCardinalityNumber(number);

                    }

                    JSONObject correllation = (JSONObject) jsonedge.get("correllation");
                    if(correllation != null) {
                        JSONObject jsonGenerator = (JSONObject)correllation.get("generator");
                        edge.setCorrellation(parseGenerator(jsonGenerator));
                    }

                }
            }
        } catch(ParseException pe) {
            throw new SyntacticException("Syntactic Exception Error: "+pe.toString());
        } catch(IOException ioe) {
            System.out.println(ioe);
        }

        return ast;
    }
}
