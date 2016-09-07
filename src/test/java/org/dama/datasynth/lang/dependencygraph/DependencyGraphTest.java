package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.TestHelpers;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.SemanticException;
import org.dama.datasynth.lang.dependencygraph.builder.DependencyGraphBuilder;
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
            assertTrue(neighbors.get(0).isType("Entity") && (((Entity)neighbors.get(0)).getName().compareTo("Person") == 0));

            neighbors = graph.getNeighbors(edgeFriendship,"target");
            assertTrue(neighbors.get(0).isType("Entity") && (((Entity)neighbors.get(0)).getName().compareTo("Person") == 0));

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
        /*Ast ast = new Ast();
        Ast.Entity entity = new Ast.Entity("person",100L);
        Ast.Generator attributeCountryGenerator = new Ast.Generator("org.dama.datasynth.generators.CDFGenerator");
        attributeCountryGenerator.addInitParameter( new Ast.Atomic("countries.txt", Types.DataType.STRING));
        attributeCountryGenerator.addInitParameter( new Ast.Atomic("0",Types.DataType.INTEGER));
        attributeCountryGenerator.addInitParameter( new Ast.Atomic("1",Types.DataType.INTEGER));
        attributeCountryGenerator.addInitParameter( new Ast.Atomic("|", Types.DataType.STRING));

        Ast.Generator attributeNameGenerator = new Ast.Generator("org.dama.datasynth.generators.CorrellationGenerator");
        attributeNameGenerator.addInitParameter(new Ast.Atomic("names.txt", Types.DataType.STRING));
        attributeNameGenerator.addInitParameter(new Ast.Atomic("0", Types.DataType.INTEGER));
        attributeNameGenerator.addInitParameter(new Ast.Atomic("1", Types.DataType.INTEGER));
        attributeNameGenerator.addInitParameter(new Ast.Atomic("|", Types.DataType.STRING));

        attributeNameGenerator.addRunParameter( new Ast.Atomic("person.country",Types.DataType.STRING));

        Ast.Attribute attributeCounty = new Ast.Attribute("person.country", Types.DataType.STRING, attributeCountryGenerator);
        Ast.Attribute attributeName = new Ast.Attribute("person.name", Types.DataType.STRING, attributeNameGenerator);

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
        */

        Ast ast = TestHelpers.testQuery("src/test/resources/testqueries/dependencyGraphTest/testquery.json");

        DependencyGraph dependencyGraph = DependencyGraphBuilder.buildDependencyGraph(ast);
        List<Entity> entities = dependencyGraph.getEntities();
        try {
            assertTrue(entities.size() == 1);
        } catch(AssertionError e) {
            System.err.println("Number of entities "+entities.size());
            throw e;
        }
        assertTrue(entities.get(0).getName().compareTo("person") == 0);
        assertTrue(entities.get(0).getType().compareTo("Entity") == 0);
        Entity person = (Entity) entities.get(0);

        List<Vertex> attributes = dependencyGraph.getNeighbors(person,"attribute");
        Attribute oidAttribute = ((Attribute)attributes.get(0));
        assertTrue(oidAttribute.getAttributeName().compareTo("person.oid") == 0);
        Attribute countryAttribute = ((Attribute)attributes.get(1));
        assertTrue(countryAttribute.getAttributeName().compareTo("person.country") == 0);
        Attribute nameAttribute = ((Attribute)attributes.get(2));
        assertTrue(nameAttribute.getAttributeName().compareTo("person.name") == 0);

        Generator generator = (Generator)dependencyGraph.getNeighbors(nameAttribute,"generator").get(0);
        List<Vertex> runParameters = dependencyGraph.getNeighbors(generator,"runParameter");
        Attribute oidRunParameter = (Attribute)runParameters.get(0);
        assertTrue(oidRunParameter.getAttributeName().compareTo("person.oid") == 0);
        Attribute countryRunParameter = (Attribute)runParameters.get(1);
        assertTrue(countryRunParameter.getAttributeName().compareTo("person.country") == 0);

        generator = (Generator)dependencyGraph.getNeighbors(countryAttribute,"generator").get(0);
        runParameters = dependencyGraph.getNeighbors(generator,"runParameter");
        oidRunParameter = (Attribute)runParameters.get(0);
        assertTrue(oidRunParameter.getAttributeName().compareTo("person.oid") == 0);

    }
}
