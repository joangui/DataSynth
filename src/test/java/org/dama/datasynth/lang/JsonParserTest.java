package org.dama.datasynth.lang;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 5/09/16.
 */
public class JsonParserTest {

    @Test
    public void testJsonParser() {

        try {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/testqueries/testquery.json"));
        Parser parser = new Parser();
            Ast ast = parser.parse(new String(encoded,"UTF8"));
            ast.doSemanticAnalysis();
        } catch(SemanticException e) {
            assertTrue(e.getMessage(),false);
        }  catch(SyntacticException e) {
            assertTrue(e.getMessage(),false);
        } catch(IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            assertTrue(false);
        } catch(Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            assertTrue(false);
        }
    }

}
