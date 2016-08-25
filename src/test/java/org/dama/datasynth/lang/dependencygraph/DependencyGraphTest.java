package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.SemanticException;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by aprat on 24/08/16.
 */
public class DependencyGraphTest {

    @Test
    public void testInitialize(){
        Ast ast = new Ast();
        Ast.Entity entity = new Ast.Entity("person",100L);
        Ast.Generator attributeCountryGenerator = new Ast.Generator("org.dama.datasynth.CDFGenerator");
        attributeCountryGenerator.addInitParameter("countries.txt");
        attributeCountryGenerator.addInitParameter("0");
        attributeCountryGenerator.addInitParameter("1");
        attributeCountryGenerator.addInitParameter("|");

        Ast.Generator attributeNameGenerator = new Ast.Generator("org.dama.datasynth.CorrellationGenerator");
        attributeNameGenerator.addInitParameter("names.txt");
        attributeNameGenerator.addInitParameter("0");
        attributeNameGenerator.addInitParameter("1");
        attributeNameGenerator.addInitParameter("|");

        attributeNameGenerator.addRunParameter("country");

        Ast.Attribute attributeCounty = new Ast.Attribute("country", Types.DATATYPE.STRING, attributeCountryGenerator);
        Ast.Attribute attributeName = new Ast.Attribute("name", Types.DATATYPE.STRING, attributeNameGenerator);

        entity.addAttribute(attributeCounty);
        entity.addAttribute(attributeName);

        ast.addEntity(entity);

        try {
            ast.doSemanticAnalysis();
        } catch (SemanticException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            assertTrue(true);
        }

        DependencyGraph dependencyGraph = new DependencyGraph(ast);
        List<Vertex> entities = dependencyGraph.getEntities();
        try {
            assertTrue(entities.size() == 1);
        } catch(AssertionError e) {
            System.err.println("Number of entities "+entities.size());
            throw e;
        }
        assertTrue(entities.get(0).getId().compareTo("person") == 0);
        assertTrue(entities.get(0).getType().compareTo("Entity") == 0);
        Entity person = (Entity) entities.get(0);

        boolean nameAttributeFound = false;
        boolean countryAttributeFound = false;
        boolean oidAttributeFound = false;

        Set<DEdge> attributes = dependencyGraph.outgoingEdgesOf(person);
        for(DEdge edge : attributes) {
            if(edge.getTarget().getType().compareTo("Attribute") == 0) {
                Attribute attribute = (Attribute) edge.getTarget();
                switch (attribute.getAttributeName()) {
                    case "oid": {
                        oidAttributeFound = true;
                    }
                    break;
                    case "name": {
                        nameAttributeFound = true;
                        Set<DEdge> attributes2 = dependencyGraph.outgoingEdgesOf(attribute);
                        boolean countryAttribute2Found = false;
                        boolean oidAttribute2Found = false;
                        for (DEdge edge2 : attributes2) {
                            if(edge2.getTarget().getType().compareTo("Attribute") == 0) {
                                Attribute attribute2 = (Attribute) edge2.getTarget();
                                switch (attribute2.getAttributeName()) {
                                    case "oid":
                                        oidAttribute2Found = true;
                                        break;
                                    case "country":
                                        countryAttribute2Found = true;
                                        break;
                                }
                            }
                        }
                        assertTrue(oidAttribute2Found && countryAttribute2Found);
                    }
                    break;
                    case "country": {
                        countryAttributeFound = true;
                        Set<DEdge> attributes2 = dependencyGraph.outgoingEdgesOf(attribute);
                        boolean oidAttribute2Found = false;
                        for (DEdge edge2 : attributes2) {
                            if(edge2.getTarget().getType().compareTo("Attribute") == 0) {
                                Attribute attribute2 = (Attribute) edge2.getTarget();
                                switch (attribute2.getAttributeName()) {
                                    case "oid":
                                        oidAttribute2Found = true;
                                        break;
                                }
                            }
                        }
                        assertTrue(oidAttribute2Found);
                    }
                    break;
                }
            }
        }
        assertTrue(nameAttributeFound && countryAttributeFound && oidAttributeFound);
    }
}
