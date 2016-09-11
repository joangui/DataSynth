package org.dama.datasynth.lang.dependencygraph;

import org.dama.datasynth.TestHelpers;
import org.dama.datasynth.common.Types;
import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.dependencygraph.builder.DependencyGraphBuilder;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by aprat on 24/08/16.
 */
public class DependencyGraphTest {

    @Test
    public void testPropertyValue() {

        Vertex.PropertyValue value = new Vertex.PropertyValue(new Boolean(true));
        assertTrue(value.getValue().compareTo("true") == 0);

        value = new Vertex.PropertyValue(new Integer(1));
        assertTrue(value.getValue().compareTo("1") == 0);

        value = new Vertex.PropertyValue(new Long(1));
        assertTrue(value.getValue().compareTo("1") == 0);

        value = new Vertex.PropertyValue(new Float(1.0));
        assertTrue(value.getValue().compareTo("1.0") == 0);

        value = new Vertex.PropertyValue(new Double(1.0));
        assertTrue(value.getValue().compareTo("1.0") == 0);

        value = new Vertex.PropertyValue(new String("test"));
        assertTrue(value.getValue().compareTo("test") == 0);

    }
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
            assertTrue(neighbors.get(0).isType("Attribute") && (((Attribute)neighbors.get(0)).getName().compareTo("Person.name") == 0));
            assertTrue(neighbors.get(1).isType("Attribute") && (((Attribute)neighbors.get(1)).getName().compareTo("Person.country") == 0));
            assertTrue(neighbors.get(2).isType("Attribute") && (((Attribute)neighbors.get(2)).getName().compareTo("Person.age") == 0));

            neighbors = graph.getNeighbors(entityPerson,"attribute");
            assertTrue(neighbors.get(0).isType("Attribute") && (((Attribute)neighbors.get(0)).getName().compareTo("Person.name") == 0));
            assertTrue(neighbors.get(1).isType("Attribute") && (((Attribute)neighbors.get(1)).getName().compareTo("Person.country") == 0));
            assertTrue(neighbors.get(2).isType("Attribute") && (((Attribute)neighbors.get(2)).getName().compareTo("Person.age") == 0));

            neighbors = graph.getNeighbors(attributeName,"dependsOn");
            assertTrue(neighbors.get(0).isType("Attribute") && (((Attribute)neighbors.get(0)).getName().compareTo("Person.country") == 0));
            assertTrue(neighbors.get(1).isType("Attribute") && (((Attribute)neighbors.get(1)).getName().compareTo("Person.age") == 0));

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
        assertTrue(oidAttribute.getName().compareTo("person.oid") == 0);
        Attribute countryAttribute = ((Attribute)attributes.get(1));
        assertTrue(countryAttribute.getName().compareTo("person.country") == 0);
        Attribute nameAttribute = ((Attribute)attributes.get(2));
        assertTrue(nameAttribute.getName().compareTo("person.name") == 0);

        Generator generator = (Generator)dependencyGraph.getNeighbors(nameAttribute,"generator").get(0);
        List<Vertex> requires = dependencyGraph.getNeighbors(generator,"requires");
        Attribute oidRunParameter = (Attribute)requires.get(0);
        assertTrue(oidRunParameter.getName().compareTo("person.oid") == 0);
        Attribute countryRunParameter = (Attribute)requires.get(1);
        assertTrue(countryRunParameter.getName().compareTo("person.country") == 0);

        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(0)).getValue().compareTo("/email.txt") == 0);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(0)).getDataType() == Types.DataType.STRING);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(1)).getValue().compareTo("0") == 0);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(1)).getDataType() == Types.DataType.LONG);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(2)).getValue().compareTo("1") == 0);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(2)).getDataType() == Types.DataType.LONG);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(3)).getValue().compareTo(" ") == 0);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(3)).getDataType() == Types.DataType.STRING);



        generator = (Generator)dependencyGraph.getNeighbors(countryAttribute,"generator").get(0);
        requires = dependencyGraph.getNeighbors(generator,"requires");
        oidRunParameter = (Attribute)requires.get(0);
        assertTrue(oidRunParameter.getName().compareTo("person.oid") == 0);

        Edge edge = dependencyGraph.getEdge("friendship.person.person");
        assertTrue(edge != null);
        assertTrue(edge.getDirection() == Types.Direction.UNDIRECTED);
        assertTrue(((Attribute)dependencyGraph.getNeighbors(edge,"source").get(0)).getName().compareTo("person.oid") == 0);
        assertTrue(((Attribute)dependencyGraph.getNeighbors(edge,"target").get(0)).getName().compareTo("person.oid") == 0);

        Attribute sourceCardinality = (Attribute)dependencyGraph.getNeighbors(edge,"sourceCardinality").get(0);
        Generator sourceCardinalityGenerator = (Generator)dependencyGraph.getNeighbors(sourceCardinality,"generator").get(0);
        oidAttribute  = (Attribute)dependencyGraph.getNeighbors(sourceCardinalityGenerator,"requires").get(0);
        assertTrue(oidAttribute.getName().compareTo("person.oid") == 0);

        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(0)).getValue().compareTo("/dicLocations.txt") == 0);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(0)).getDataType() == Types.DataType.STRING);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(1)).getValue().compareTo("1") == 0);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(1)).getDataType() == Types.DataType.LONG);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(2)).getValue().compareTo("5") == 0);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(2)).getDataType() == Types.DataType.LONG);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(3)).getValue().compareTo(" ") == 0);
        assertTrue(((Literal)dependencyGraph.getNeighbors(generator,"initparam").get(3)).getDataType() == Types.DataType.STRING);

        countryAttribute = (Attribute)dependencyGraph.getNeighbors(edge,"correlates").get(0);
        assertTrue(countryAttribute.getName().compareTo("person.country") == 0);

        nameAttribute  = (Attribute)dependencyGraph.getNeighbors(edge,"correlates").get(1);
        assertTrue(nameAttribute.getName().compareTo("person.name") == 0);



        edge = dependencyGraph.getEdge("parent.person.person");
        assertTrue(edge != null);
        assertTrue(edge.getDirection() == Types.Direction.DIRECTED);
        assertTrue(((Attribute)dependencyGraph.getNeighbors(edge,"source").get(0)).getName().compareTo("person.oid") == 0);
        assertTrue(((Attribute)dependencyGraph.getNeighbors(edge,"target").get(0)).getName().compareTo("person.oid") == 0);

        sourceCardinality = (Attribute)dependencyGraph.getNeighbors(edge,"sourceCardinality").get(0);
        sourceCardinalityGenerator = (Generator)dependencyGraph.getNeighbors(sourceCardinality,"generator").get(0);
        oidAttribute  = (Attribute)dependencyGraph.getNeighbors(sourceCardinalityGenerator,"requires").get(0);
        assertTrue(oidAttribute.getName().compareTo("person.oid") == 0);

        Literal targetCardinality = (Literal)dependencyGraph.getNeighbors(edge,"targetCardinality").get(0);
        assertTrue(targetCardinality.getValue().compareTo("1") == 0 && (targetCardinality.getDataType() == Types.DataType.LONG));

        countryAttribute  = (Attribute)dependencyGraph.getNeighbors(edge,"correlates").get(0);
        assertTrue(countryAttribute.getName().compareTo("person.country") == 0);

    }
}
