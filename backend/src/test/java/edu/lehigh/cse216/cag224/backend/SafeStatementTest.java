package edu.lehigh.cse216.cag224.backend;

import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SafeStatementTest extends TestCase {
    public SafeStatementTest(String testName){
        super(testName);
    }

    public static Test suite(){
        return new TestSuite(SafeStatementTest.class);
    }

    public void testAll(){
        try {
            SafeStatement.TableMapping tblMessages = new SafeStatement.TableMapping("tblMessages", "testTblMessages");
            SafeStatement.TableMapping tblComments = new SafeStatement.TableMapping("tblComments", "testTblComments");
            SafeStatement.TableMapping tblUsers = new SafeStatement.TableMapping("tblUsers", "testTblUsers");
            SafeStatement.TableMapping tblLikes = new SafeStatement.TableMapping("tblLikes", "testTblLikes");
            ArrayList<SafeStatement.TableMapping> mapping = new ArrayList<SafeStatement.TableMapping>();
            mapping.add(tblMessages);
            mapping.add(tblComments);
            mapping.add(tblUsers);
            mapping.add(tblLikes);
            SafeStatement.setTableMapping(mapping);

            Database.useTestTables = true;
            Database db = Database.getDatabase(System.getenv("DATABASE_URL"), "5432");
            SafeStatement.conn = db.mConnection;
            SafeStatement statement = new SafeStatement("SELECT * FROM tblUsers WHERE userid == {int, i1} AND isbanned == {BOOL,b1} AND email == {Str , s1}", true);
            statement
                .setBool("b1", false)
                .setInt("i1", 0)
                .setString("s1", "email");
            assertEquals(statement.toString(), "SELECT * FROM testTblUsers WHERE userid == ? AND isbanned == ? AND email == ?");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertTrue(false);
        }
        assertTrue(true);
    }
}
