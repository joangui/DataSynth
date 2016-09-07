package org.dama.datasynth.lang;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.dama.datasynth.TestHelpers.testQuery;
import static org.dama.datasynth.TestHelpers.testSemanticError;
import static org.dama.datasynth.TestHelpers.testSyntacticError;
import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 5/09/16.
 */
public class JsonParserTest {



    @Test
    public void testJsonParser() {
        testQuery("src/test/resources/testqueries/jsonParserTest/testquery.json");
    }

    @Test
    public void testSyntaxAttributeTypes() {
        testQuery("src/test/resources/testqueries/jsonParserTest/testquery_attribute_type_string.json");
        testQuery("src/test/resources/testqueries/jsonParserTest/testquery_attribute_type_boolean.json");
        testQuery("src/test/resources/testqueries/jsonParserTest/testquery_attribute_type_float.json");
        testQuery("src/test/resources/testqueries/jsonParserTest/testquery_attribute_type_double.json");
        testQuery("src/test/resources/testqueries/jsonParserTest/testquery_attribute_type_integer.json");
        testQuery("src/test/resources/testqueries/jsonParserTest/testquery_attribute_type_long.json");
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_attribute_type_invalid.json", SyntacticException.SyntacticExceptionType.INVALID_ATTRIBUTE_TYPE);
    }

    @Test
    public void testSyntaxEntityFields() {
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_entity_name_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_entity_number_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_entity_name_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_entity_number_value_type_no_long.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
    }

    @Test
    public void testSyntaxAttributeFields() {
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_attribute_name_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_attribute_type_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_attribute_generator_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_attribute_type_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_attribute_name_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
    }

    @Test
    public void testSyntaxEdgeFields() {
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_number_value_type_no_long.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_name_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_direction_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_direction_invalid.json", SyntacticException.SyntacticExceptionType.INVALID_DIRECTION_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_source_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_target_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_name_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_direction_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_source_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_target_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
    }

    @Test
    public void testSyntaxGeneratorFields() {
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_generator_name_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_generator_name_value_type_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_generator_runparameters_no_string.json", SyntacticException.SyntacticExceptionType.INVALID_FIELD_TYPE);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_generator_runparameters_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
        testSyntacticError("src/test/resources/testqueries/jsonParserTest/testquery_generator_initparameters_missing.json", SyntacticException.SyntacticExceptionType.MISSING_FIELD);
    }

    @Test
    public void testSemanticAttributeNames(){
        testSemanticError("src/test/resources/testqueries/jsonParserTest/testquery_attribute_name_repeated.json", SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_REPEATED);
        testSemanticError("src/test/resources/testqueries/jsonParserTest/testquery_attribute_name_oid.json", SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_OID);
    }
    @Test
    public void testSemanticGenerator(){
        testSemanticError("src/test/resources/testqueries/jsonParserTest/testquery_generator_name_invalid.json", SemanticException.SemanticExceptionType.GENERATOR_NOT_EXISTS);
        testSemanticError("src/test/resources/testqueries/jsonParserTest/testquery_generator_runparameters_invalid.json", SemanticException.SemanticExceptionType.ATTRIBUTE_NAME_UNEXISTING);
    }

    @Test
    public void testSemanticEdge() {
        testSemanticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_source_not_exists.json", SemanticException.SemanticExceptionType.EDGE_ENDPOINT_NOT_EXISTS);
        testSemanticError("src/test/resources/testqueries/jsonParserTest/testquery_edge_target_not_exists.json", SemanticException.SemanticExceptionType.EDGE_ENDPOINT_NOT_EXISTS);
    }
}
