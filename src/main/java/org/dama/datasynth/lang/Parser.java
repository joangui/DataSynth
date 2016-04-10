package org.dama.datasynth.lang;

import java.io.StringReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Parser {

    /**
     * Parses a data definition schema
     * @param str
     * @return
     */
    public Ast parse( String str ) {
        Ast ast = new Ast();
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new StringReader(str));
            JSONArray entities = (JSONArray)jsonObject.get("entities");

            for( Object obj : entities ) {
                JSONObject  entity= (JSONObject) obj;
                String entityName = (String)entity.get("name");
                Ast.Entity ent = new Ast.Entity(entityName);
                JSONArray attributes = (JSONArray) entity.get("attributes");
                for( Object objj : attributes ) {
                    JSONObject attribute = (JSONObject)objj;
                    JSONObject generator = (JSONObject)attribute.get("generator");
                    Ast.Generator gen = new Ast.Generator((String)generator.get("name"));
                    JSONArray runParameters = (JSONArray)generator.get("runParameters");
                    for(Object runParameter: runParameters) {
                        gen.addRunParameter((String)(runParameter));
                    }

                    JSONArray initParameters = (JSONArray)generator.get("initParameters");
                    for(Object initParameter: initParameters) {
                        gen.addInitParameter((String)(initParameter));
                    }

                    Ast.Attribute attr = new Ast.Attribute(
                            (String)attribute.get("name"),
                            Ast.DATATYPE.fromString((String)attribute.get("type")),
                            gen
                    );
                    ent.addAttribute(attr);
                }
                ast.addEntity(ent);
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
