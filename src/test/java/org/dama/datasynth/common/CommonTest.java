package org.dama.datasynth.common;

import org.dama.datasynth.runtime.Generator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by aprat on 18/08/16.
 */
public class CommonTest {


    @Test
    public void DATATYPETest(){

        DATATYPETestAssertEqualsHelper("integer",Types.DATATYPE.INTEGER);
        DATATYPETestAssertEqualsHelper("Integer",Types.DATATYPE.INTEGER);
        DATATYPETestAssertEqualsHelper("INteGER",Types.DATATYPE.INTEGER);

        DATATYPETestAssertEqualsHelper("long",Types.DATATYPE.LONG);
        DATATYPETestAssertEqualsHelper("Long",Types.DATATYPE.LONG);
        DATATYPETestAssertEqualsHelper("LONg",Types.DATATYPE.LONG);

        DATATYPETestAssertEqualsHelper("boolean",Types.DATATYPE.BOOLEAN);
        DATATYPETestAssertEqualsHelper("Boolean",Types.DATATYPE.BOOLEAN);
        DATATYPETestAssertEqualsHelper("BoOLean",Types.DATATYPE.BOOLEAN);

        DATATYPETestAssertEqualsHelper("float",Types.DATATYPE.FLOAT);
        DATATYPETestAssertEqualsHelper("Float",Types.DATATYPE.FLOAT);
        DATATYPETestAssertEqualsHelper("fLoAt",Types.DATATYPE.FLOAT);

        DATATYPETestAssertEqualsHelper("double",Types.DATATYPE.DOUBLE);
        DATATYPETestAssertEqualsHelper("Double",Types.DATATYPE.DOUBLE);
        DATATYPETestAssertEqualsHelper("DoUble",Types.DATATYPE.DOUBLE);

        DATATYPETestAssertEqualsHelper("string",Types.DATATYPE.STRING);
        DATATYPETestAssertEqualsHelper("String",Types.DATATYPE.STRING);
        DATATYPETestAssertEqualsHelper("StRing",Types.DATATYPE.STRING);

        DATATYPETestAssertNotEqualsHelper("strng", Types.DATATYPE.STRING);

    }

    private void DATATYPETestAssertEqualsHelper(String text, Types.DATATYPE type) {
        assertEquals("Unable to find type "+text, Types.DATATYPE.fromString(text),type);
    }

    private void DATATYPETestAssertNotEqualsHelper(String text, Types.DATATYPE type) {
        assertNotEquals("Type "+text+" should not exist", Types.DATATYPE.fromString(text),type);
    }

    @Test
    public void GeneratorTest() {
        Generator generator = Types.Generator("org.dama.datasynth.generators.CDFGenerator");
        assertNotEquals("Generator org.dama.datasynth.generators.CDFGenerator should exist", generator, null);
        List<Types.DATATYPE> parameters = new ArrayList<Types.DATATYPE>();
        try {
            parameters.add(Types.DATATYPE.STRING);
            parameters.add(Types.DATATYPE.STRING);
            parameters.add(Types.DATATYPE.STRING);
            parameters.add(Types.DATATYPE.STRING);
            assertNotEquals("Function CDFGenerator.initialize with parameters String, String, String, String does not exist", Types.GetMethod(generator, "initialize", parameters, null), null);
            assertNotEquals("Function CDFGenerator.initialize ", Types.GetUntypedMethod(generator, "initialize"), null);
            parameters.clear();
            parameters.add(Types.DATATYPE.LONG);
            assertNotEquals("Function CDFGenerator.run with Long and return type String does not exist", Types.GetMethod(generator, "run", parameters, Types.DATATYPE.STRING), null);
        } catch(CommonException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
