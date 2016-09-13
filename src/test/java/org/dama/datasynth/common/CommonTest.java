package org.dama.datasynth.common;

import org.dama.datasynth.generators.Generator;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 18/08/16.
 */
public class CommonTest {


    @Test
    public void DATATYPETest(){

        DATATYPETestAssertEqualsHelper("integer", Types.DataType.INTEGER);
        DATATYPETestAssertEqualsHelper("Integer", Types.DataType.INTEGER);
        DATATYPETestAssertEqualsHelper("INteGER", Types.DataType.INTEGER);

        DATATYPETestAssertEqualsHelper("long", Types.DataType.LONG);
        DATATYPETestAssertEqualsHelper("Long", Types.DataType.LONG);
        DATATYPETestAssertEqualsHelper("LONg", Types.DataType.LONG);

        DATATYPETestAssertEqualsHelper("boolean", Types.DataType.BOOLEAN);
        DATATYPETestAssertEqualsHelper("Boolean", Types.DataType.BOOLEAN);
        DATATYPETestAssertEqualsHelper("BoOLean", Types.DataType.BOOLEAN);

        DATATYPETestAssertEqualsHelper("float", Types.DataType.FLOAT);
        DATATYPETestAssertEqualsHelper("Float", Types.DataType.FLOAT);
        DATATYPETestAssertEqualsHelper("fLoAt", Types.DataType.FLOAT);

        DATATYPETestAssertEqualsHelper("double", Types.DataType.DOUBLE);
        DATATYPETestAssertEqualsHelper("Double", Types.DataType.DOUBLE);
        DATATYPETestAssertEqualsHelper("DoUble", Types.DataType.DOUBLE);

        DATATYPETestAssertEqualsHelper("string", Types.DataType.STRING);
        DATATYPETestAssertEqualsHelper("String", Types.DataType.STRING);
        DATATYPETestAssertEqualsHelper("StRing", Types.DataType.STRING);

        DATATYPETestAssertNotEqualsHelper("strng", Types.DataType.STRING);


        DATATYPETestFromObjectHelper(new Boolean(true), "boolean");
        DATATYPETestFromObjectHelper(new Integer(1), "integer");
        DATATYPETestFromObjectHelper(new Long(1), "long");
        DATATYPETestFromObjectHelper(new Float(1.0), "float");
        DATATYPETestFromObjectHelper(new Double(1.0), "double");
        DATATYPETestFromObjectHelper(new String("test"), "string");

    }

    private <T> void DATATYPETestFromObjectHelper(T var, String type) {
        Types.DataType dataType = Types.DataType.fromObject(var);
        assertEquals(dataType,Types.DataType.fromString(type));
    }

    private void DATATYPETestAssertEqualsHelper(String text, Types.DataType type) {
        assertEquals("Unable to find type "+text, Types.DataType.fromString(text),type);
    }

    private void DATATYPETestAssertNotEqualsHelper(String text, Types.DataType type) {
        assertNotEquals("Type "+text+" should not exist", Types.DataType.fromString(text),type);
    }

    @Test
    public void GeneratorTest() {
        Generator generator = null;
        try {
            generator = Types.getGenerator("org.dama.datasynth.generators.CDFGenerator");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Assert.fail("Test failed due to exception");
            System.exit(1);
        } catch (InstantiationException e) {
            e.printStackTrace();
            Assert.fail("Test failed due to exception");
            System.exit(1);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Assert.fail("Test failed due to exception");
            System.exit(1);
        }
        assertNotEquals("Generator org.dama.datasynth.generators.CDFGenerator should exist", generator, null);
        List<Types.DataType> parameters = new ArrayList<Types.DataType>();
        try {
            parameters.add(Types.DataType.STRING);
            parameters.add(Types.DataType.LONG);
            parameters.add(Types.DataType.LONG);
            parameters.add(Types.DataType.STRING);
            assertNotEquals("Function CDFGenerator.initialize with parameters String, String, String, String does not exist", Types.getMethod(generator, "initialize", parameters, null), null);
            assertNotEquals("Function CDFGenerator.initialize ", Types.getUntypedMethod(generator, "initialize"), null);
            parameters.clear();
            parameters.add(Types.DataType.LONG);
            assertNotEquals("Function CDFGenerator.run with Long and return type String does not exist", Types.getMethod(generator, "run", parameters, Types.DataType.STRING), null);
        } catch (Exception e) {
            Assert.fail("Test failed due to exception");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
