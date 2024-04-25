package edu.lehigh.cse216.mfs409.admin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Private method for test methods to call when they are run so they can connec
     * to the DB
     * 
     * @return a Database object on success, null on failure
     */
    private Database getDatabaseFromEnv() {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");

        // attempt to connect to the database
        return Database.getDatabase(ip, port, user, pass);
    }

    /**
     * Test if we can connect to the database
     * To run, run command and fill in appropriate info: POSTGRES_IP=<>
     * POSTGRES_PORT=5432 POSTGRES_USER=<> POSTGRES_PASS=<> mvn
     * -Dtest=AppTest#testDatabaseConnection test
     */
    public void testDatabaseConnection() {
        // attempt to connect to DB
        Database db = getDatabaseFromEnv();
        assertTrue("good connection", db != null);

        db.disconnect();
    }

    /**
     * Test that tables can be created
     * To run, run command: POSTGRES_IP=<> POSTGRES_PORT=5432 POSTGRES_USER=<>
     * POSTGRES_PASS=<> mvn -Dtest=AppTest#testCreateTable test
     */
    public void testCreateTable() {
        // connect to DB
        Database db = getDatabaseFromEnv();

        if (db == null) {
            assertFalse("unable to connect to database", false);
            return;
        }

        // drop table so it can be created without an error
        db.dropTable();

        // attempt to create tables
        db.createTable();

        // make sure tables can be accessed
        assertTrue("created tables", db.getAllMessages(-1) != null);

        db.disconnect();
    }

    /**
     * Test that invalidating message removes it from DB
     * To run, run command: POSTGRES_IP=<> POSTGRES_PORT=5432 POSTGRES_USER=<>
     * POSTGRES_PASS=<> mvn -Dtest=AppTest#testInvalidateMessage test
     */
    public void testInvalidateMessage() {
        // connect to database
        Database db = getDatabaseFromEnv();

        if (db == null) {
            assertFalse("unable to connect to database", false);
            return;
        }

        // drop table to make sure message ID is 1
        db.dropTable();
        // create the table again
        db.createTable();
        // insert row to delete, should be message ID 1
        db.insertMessage(-1, "Testing deleting row");
        // attempt to delete row
        db.deleteMessage(1);

        assertTrue(db.getOneMessage(1, -1) == null);

        db.disconnect();
    }

    /**
     * Test that the drop command deletes the tables
     * To run, run command: POSTGRES_IP=<> POSTGRES_PORT=5432 POSTGRES_USER=<>
     * POSTGRES_PASS=<> mvn -Dtest=AppTest#testInvalidateMessage test
     */
    public void testDeleteTable() {
        // connect to DB
        Database db = getDatabaseFromEnv();

        if (db == null) {
            assertFalse("unable to connect to database", false);
            return;
        }

        // attempt to drop the table
        db.dropTable();

        // make sure tables cannot be accessed
        assertTrue("created tables", db.getAllMessages(-1) == null);

        db.disconnect();
    }

    // public void gettingFiles(){
    //     /* Load pre-authorized user credentials from the environment.*/
    //     GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
    //     HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

    //     // Build a new authorized API client service.
    //     Drive service = new Drive.Builder(new NetHttpTransport(),GsonFactory.getDefaultInstance(),requestInitializer).setApplicationName("Drive samples").build();
    //     try {
    //         FileList result = service.files().list()
    //         .setPageSize(10)
    //         .setFields("nextPageToken, files(id, name, modifiedTime)")
    //         .execute();
    //         List<File> files = result.getFiles();
    //         assertTrue(file != null);
    //     } catch (GoogleJsonResponseException e) {
    //         System.err.println("Unable to get file: " + e.getDetails());
    //         throw e;
    //     }
    // }

}
