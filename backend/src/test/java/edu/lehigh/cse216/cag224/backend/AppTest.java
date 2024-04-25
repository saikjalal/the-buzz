package edu.lehigh.cse216.cag224.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
// import java.util.Map;
/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * To run this test you will want to run the command below with the correct url.
     * PORT=8998 DATABASE_URL={insert testing database url here} mvn -Dtest=AppTest#testApp test
     * @return "good connection" if the app is able to properly connect with the given database url
     */
    public void testApp()
    {
        // Map<String, String> env = System.getenv();
        // String port = env.get("PORT");
        Database.useTestTables = true;
        Database db = Database.getDatabase(System.getenv("DATABASE_URL"), "5432");
        assertTrue("good connection", db != null);
        db.disconnect();
    }
    
}
