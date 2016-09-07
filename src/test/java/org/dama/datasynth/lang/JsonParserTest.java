package org.dama.datasynth.lang;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 5/09/16.
 */
public class JsonParserTest {

    private void testQueryHelper(String queryPath) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(queryPath));
            Parser parser = new Parser();
            Ast ast = parser.parse(new String(encoded,"UTF8"));
            ast.doSemanticAnalysis();
        } catch(SemanticException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }  catch(SyntacticException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        } catch(IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
            return;
        }
        return;
    }

    private void testSyntacticError(String queryPath, SyntacticException.SyntacticExceptionType type) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(queryPath));
            Parser parser = new Parser();
            Ast ast = parser.parse(new String(encoded,"UTF8"));
        } catch(SemanticException e) {
            e.printStackTrace();
        }  catch(SyntacticException e) {
            if(e.getType() == type) {
                assertTrue(true);
                return;
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        assertTrue(false);
        return;
    }

    private void testSemanticError(String queryPath, SemanticException.SemanticExceptionType type) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(queryPath));
            Parser parser = new Parser();
            Ast ast = parser.parse(new String(encoded,"UTF8"));
            ast.doSemanticAnalysis();
        } catch(SemanticException e) {
            if(e.getType() == type) {
                assertTrue(true);
                return;
            }
        }  catch(SyntacticException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        assertTrue(false);
        return;
    }

    @Test
    public void testJsonParser() {
        testQueryHelper("src/test/resources/testqueries/testquery.json");
    }

    @Test
    public void testSyntaxAttributeTypes() {
        testQueryHelper("src/test/resources/testqueries/testquery_attribute_type_string.json");
        testQueryHelper("src/test/resources/testqueries/testquery_attribute_type_boolean.json");
        testQueryHelper("src/test/resources/testqueries/testquery_attribute_type_float.json");
        testQueryHelper("src/test/resources/testqueries/testquery_attribute_type_double.json");
        testQueryHelper("src/test/resources/testqueries/testquery_attribute_type_integer.json");
        testQueryHelper("src/test/resources/testqueries/testquery_attribute_type_long.json");
        testSyntacticError("src/test/resources/testqueries/testquery_attribute_type_invalid.json", SyntacticException.SyntacticExceptionType.INVALID_ATTRIBUTE_TYPE);
    }

    @Test
    public void testSyntaxEntityFields() {
        testSyntacticError("src/test/resources/testqueries/testquery_entity_name_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_entity_number_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_entity_name_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_entity_number_value_type_no_long.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
    }

    @Test
    public void testSyntaxAttributeFields() {
        testSyntacticError("src/test/resources/testqueries/testquery_attribute_name_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_attribute_type_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_attribute_generator_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_attribute_type_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_attribute_name_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
    }

    @Test
    public void testSyntaxEdgeFields() {
        testSyntacticError("src/test/resources/testqueries/testquery_edge_number_value_type_no_long.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_edge_name_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_edge_direction_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_edge_direction_invalid.json", SyntacticException.SyntacticExceptionType.INVALID_DIRECTION_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_edge_source_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_edge_target_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_edge_name_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_edge_direction_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_edge_source_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_edge_target_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
    }

    @Test
    public void testSyntaxGeneratorFields() {
        testSyntacticError("src/test/resources/testqueries/testquery_generator_name_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_generator_name_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_generator_runparameters_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/testquery_generator_runparameters_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/testquery_generator_initparameters_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
    }

    @Test
    public void testSemanticAttributeNames(){
        testSemanticError("src/test/resources/testqueries/testquery_attribute_name_repeated.json", SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_REPEATED);
        testSemanticError("src/test/resources/testqueries/testquery_attribute_name_oid.json", SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_OID);
    }
    @Test
    public void testSemanticGenerator(){
        testSemanticError("src/test/resources/testqueries/testquery_generator_name_invalid.json", SemanticException.SemanticExceptionType.GENERATOR_NOT_EXISTS);
        testSemanticError("src/test/resources/testqueries/testquery_generator_runparameters_invalid.json", SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_UNEXISTING);
    }

    @Test
    public void testSemanticEdge() {
        testSemanticError("src/test/resources/testqueries/testquery_edge_source_not_exists.json", SemanticException.SemanticExceptionType.EDGE_ENDPOINT_NOT_EXISTS);
        testSemanticError("src/test/resources/testqueries/testquery_edge_target_not_exists.json", SemanticException.SemanticExceptionType.EDGE_ENDPOINT_NOT_EXISTS);
    }
}
