package org.dama.datasynth.lang;

import org.dama.datasynth.common.Types;
import java.io.StringReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mortbay.util.ajax.JSON;

public class Parser {

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
                            gen.addRunParameter((String) (runParameter));
                        }

                        JSONArray initParameters = (JSONArray) generator.get("initParameters");
                        for (Object initParameter : initParameters) {
                            gen.addInitParameter(initParameter.toString());
                        }

                        Ast.Attribute attr = new Ast.Attribute(
                                (String) attribute.get("name"),
                                Types.DATATYPE.fromString((String) attribute.get("type")),
                                gen
                        );
                        System.out.println("Inserting " + ent.getName() + "." + attr.getName());
                        hmAttr.put(ent.getName() + "." + attr.getName(), attr);
                        if (attr.getType() == null)
                            throw new SyntacticException(((String) attribute.get("type")) + " is not a valid data type ");

                        ent.addAttribute(attr);
                    }
                }
                ast.addEntity(ent);
            }

            for(Map.Entry<String, Ast.Attribute> entry : hmAttr.entrySet()){
                System.out.println("Key " + entry.getKey() + " IS NULL? "+ (entry.getValue() == null));
            }

            //EDGE PROCESSING
            JSONArray edges = (JSONArray)jsonObject.get("edges");
            if(edges != null) {
                for (Object obj : edges) {
                    JSONObject edge = (JSONObject) obj;
                    String edgeName = (String) edge.get("name");
                    JSONObject generator = (JSONObject) edge.get("generator");
                    Ast.Generator gen = new Ast.Generator((String) generator.get("name"));
                    JSONArray runParameters = (JSONArray) generator.get("runParameters");
                    for (Object runParameter : runParameters) {
                        gen.addRunParameter((String) (runParameter));
                    }

                    JSONArray initParameters = (JSONArray) generator.get("initParameters");
                    for (Object initParameter : initParameters) {
                        gen.addInitParameter(initParameter.toString());
                    }
                    Ast.Edge edg = new Ast.Edge(edgeName, gen);
                    JSONObject source = (JSONObject) edge.get("source");
                    JSONObject target = (JSONObject) edge.get("target");
                    Ast.Entity attrEnt = hm.get((String) source.get("entity"));
                    edg.setOrigin(attrEnt);
                    for (Object objattr : (JSONArray) source.get("attributes")) {
                        String attr = (String) objattr;
                        System.out.println("Fetching " + attrEnt.getName() + "." + attr);
                        edg.addAttributeOrigin(hmAttr.get(attrEnt.getName() + "." + attr));
                    }
                    attrEnt = hm.get((String) target.get("entity"));
                    edg.setDestination(attrEnt);
                    for (Object objattr : (JSONArray) target.get("attributes")) {
                        String attr = (String) objattr;
                        edg.addAttributeTarget(hmAttr.get(attrEnt.getName() + "." + attr));
                    }
                    ast.addEdge(edg);
                /*JSONArray cardinality = (JSONArray)edge.get("cardinality");
                int i = 0;
                for(Object c: cardinality) {
                    edg.setCardinality(Integer.parseInt(c.toString()),i);
                    ++i;
                }*/
                }
            }
        } catch(ParseException pe) {
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        } catch(IOException ioe) {
            System.out.println(ioe);
        }

        return ast;
    }
}
