package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.SemanticException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by aprat on 24/08/16.
 */
public class DependencyGraphTest {

    @Test
    public void testMethods() {

        Entity entityPerson = new Entity("Person",100);
        Attribute attributeName = new Attribute("Person.name", Types.DataType.STRING);
        Attribute attributeCountry = new Attribute("Person.country", Types.DataType.STRING);
        Attribute attributeAge = new Attribute("Person.age", Types.DataType.INTEGER);
        Edge edgeFriendship = new Edge("Person-Friendship-Person", Types.Direction.UNDIRECTED);

        DependencyGraph graph = new DependencyGraph();
        try {
            graph.addEntityVertex(entityPerson);
            graph.addAttributeVertex(attributeName);
            graph.addAttributeVertex(attributeCountry);
            graph.addAttributeVertex(attributeAge);
            graph.addEdgeVertex(edgeFriendship);

            graph.addDependency(entityPerson,attributeName,"attribute");
            graph.addDependency(entityPerson,attributeCountry,"attribute");
            graph.addDependency(entityPerson,attributeAge,"attribute");

            graph.addDependency(attributeName,attributeCountry,"dependsOn");
            graph.addDependency(attributeName,attributeAge,"dependsOn");

            graph.addDependency(edgeFriendship,entityPerson,"source");
            graph.addDependency(edgeFriendship,entityPerson,"target");

            graph.getEntity("Person");
            graph.getAttribute("Person.country");
            graph.getAttribute("Person.name");
            graph.getAttribute("Person.age");

            graph.getEdge("Person-Friendship-Person");

            List<Vertex> neighbors = graph.getNeighbors(entityPerson);
            assertTrue(neighbors.get(0).isType("Attribute") && (((Attribute)neighbors.get(0)).getAttributeName().compareTo("Person.name") == 0));
            assertTrue(neighbors.get(1).isType("Attribute") && (((Attribute)neighbors.get(1)).getAttributeName().compareTo("Person.country") == 0));
            assertTrue(neighbors.get(2).isType("Attribute") && (((Attribute)neighbors.get(2)).getAttributeName().compareTo("Person.age") == 0));

            neighbors = graph.getNeighbors(entityPerson,"attribute");
            assertTrue(neighbors.get(0).isType("Attribute") && (((Attribute)neighbors.get(0)).getAttributeName().compareTo("Person.name") == 0));
            assertTrue(neighbors.get(1).isType("Attribute") && (((Attribute)neighbors.get(1)).getAttributeName().compareTo("Person.country") == 0));
            assertTrue(neighbors.get(2).isType("Attribute") && (((Attribute)neighbors.get(2)).getAttributeName().compareTo("Person.age") == 0));

            neighbors = graph.getNeighbors(attributeName,"dependsOn");
            assertTrue(neighbors.get(0).isType("Attribute") && (((Attribute)neighbors.get(0)).getAttributeName().compareTo("Person.country") == 0));
            assertTrue(neighbors.get(1).isType("Attribute") && (((Attribute)neighbors.get(1)).getAttributeName().compareTo("Person.age") == 0));

            neighbors = graph.getNeighbors(edgeFriendship,"source");
            assertTrue(neighbors.get(0).isType("Entity") && (((Attribute)neighbors.get(0)).getAttributeName().compareTo("Person") == 0));

            neighbors = graph.getNeighbors(edgeFriendship,"target");
            assertTrue(neighbors.get(0).isType("Entity") && (((Attribute)neighbors.get(0)).getAttributeName().compareTo("Person") == 0));

        } catch(DependencyGraphConstructionException e) {
            assertTrue(e.getMessage(),false);
        }


            assertEquals(graph.getEntity("Message"),null);
            assertEquals(graph.getAttribute("country"),null);
            assertEquals(graph.getEdge("friendship"),null);

        Entity entityMessage = new Entity("Message",100);
        boolean exceptionThrown = false;
        try {
            graph.getNeighbors(entityMessage);
        } catch(DependencyGraphConstructionException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            graph.addDependency(entityMessage,entityPerson,"dependsOn");
        } catch(DependencyGraphConstructionException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            graph.addDependency(entityPerson,entityMessage,"dependsOn");
        } catch(DependencyGraphConstructionException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }

    @Test
    public void testInitialize(){
        Ast ast = new Ast();
        Ast.Entity entity = new Ast.Entity("person",100L);
        Ast.Generator attributeCountryGenerator = new Ast.Generator("org.dama.datasynth.CDFGenerator");
        attributeCountryGenerator.addInitParameter( new Ast.Atomic("countries.txt", Types.DataType.STRING));
        attributeCountryGenerator.addInitParameter( new Ast.Atomic("0",Types.DataType.INTEGER));
        attributeCountryGenerator.addInitParameter( new Ast.Atomic("1",Types.DataType.INTEGER));
        attributeCountryGenerator.addInitParameter( new Ast.Atomic("|", Types.DataType.STRING));

        Ast.Generator attributeNameGenerator = new Ast.Generator("org.dama.datasynth.CorrellationGenerator");
        attributeNameGenerator.addInitParameter(new Ast.Atomic("names.txt", Types.DataType.STRING));
        attributeNameGenerator.addInitParameter(new Ast.Atomic("0", Types.DataType.INTEGER));
        attributeNameGenerator.addInitParameter(new Ast.Atomic("1", Types.DataType.INTEGER));
        attributeNameGenerator.addInitParameter(new Ast.Atomic("|", Types.DataType.STRING));

        attributeNameGenerator.addRunParameter( new Ast.Atomic("country",Types.DataType.STRING));

        Ast.Attribute attributeCounty = new Ast.Attribute("country", Types.DataType.STRING, attributeCountryGenerator);
        Ast.Attribute attributeName = new Ast.Attribute("name", Types.DataType.STRING, attributeNameGenerator);

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

        assertTrue("Test not implemented",false);

        /*
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

        Set<DirectedEdge> attributes = dependencyGraph.outgoingEdgesOf(person);
        for(DirectedEdge edge : attributes) {
            if(dependencyGraph.getEdgeTarget(edge).getType().compareTo("Attribute") == 0) {
                Attribute attribute = (Attribute) dependencyGraph.getEdgeTarget(edge);
                switch (attribute.getAttributeName()) {
                    case "oid": {
                        oidAttributeFound = true;
                    }
                    break;
                    case "name": {
                        nameAttributeFound = true;
                        Set<DirectedEdge> attributes2 = dependencyGraph.outgoingEdgesOf(attribute);
                        boolean countryAttribute2Found = false;
                        boolean oidAttribute2Found = false;
                        for (DirectedEdge edge2 : attributes2) {
                            if(dependencyGraph.getEdgeTarget(edge2).getType().compareTo("Attribute") == 0) {
                                Attribute attribute2 = (Attribute) dependencyGraph.getEdgeTarget(edge2);
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
                        Set<DirectedEdge> attributes2 = dependencyGraph.outgoingEdgesOf(attribute);
                        boolean oidAttribute2Found = false;
                        for (DirectedEdge edge2 : attributes2) {
                            if(dependencyGraph.getEdgeTarget(edge2).getType().compareTo("Attribute") == 0) {
                                Attribute attribute2 = (Attribute) dependencyGraph.getEdgeTarget(edge2);
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
        */
    }
}
