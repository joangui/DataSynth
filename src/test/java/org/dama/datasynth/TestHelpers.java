package org.dama.datasynth;

import org.dama.datasynth.lang.Ast;
import org.dama.datasynth.lang.Parser;
import org.dama.datasynth.lang.SemanticException;
import org.dama.datasynth.lang.SyntacticException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Created by aprat on 7/09/16.
 */
public class TestHelpers {

    public static void testSyntacticError(String queryPath, SyntacticException.SyntacticExceptionType type) {
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

    public static void testSemanticError(String queryPath, SemanticException.SemanticExceptionType type) {
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


    public static Ast testQuery(String queryPath) {
        Ast ast = null;
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(queryPath));
            Parser parser = new Parser();
            ast = parser.parse(new String(encoded,"UTF8"));
            ast.doSemanticAnalysis();
        } catch(SemanticException e) {
            e.printStackTrace();
            assertTrue(false);
            return ast;
        }  catch(SyntacticException e) {
            e.printStackTrace();
            assertTrue(false);
            return ast;
        } catch(IOException e) {
            e.printStackTrace();
            assertTrue(false);
            return ast;
        } catch(Exception e) {
            e.printStackTrace();
            assertTrue(false);
            return ast;
        }
        return ast;
    }
}
