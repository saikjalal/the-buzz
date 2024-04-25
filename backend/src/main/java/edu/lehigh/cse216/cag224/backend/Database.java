package edu.lehigh.cse216.cag224.backend;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;

public class Database {
    // The connection to the database
    protected Connection mConnection;

    // Messages stuff
    private static SQLMessage messagesDatabaseInstance;
    public SQLMessage messagesTable(){
        return messagesDatabaseInstance;
    }

    // Likes stuff
    private static SQLLike likesDatabaseInstance;
    public SQLLike likesTable(){
        return likesDatabaseInstance;
    }

    // User stuff
    private static SQLUser usersDatabaseInstance;
    public SQLUser usersTable(){
        return usersDatabaseInstance;
    }

    // Comment stuff
    private static SQLComment commentsDatabaseInstance;
    public SQLComment commentsTable(){
        return commentsDatabaseInstance;
    }

    // Admin Table Stuff
    private PreparedStatement createTables;
    private PreparedStatement dropTables;

    // If to use testtables when construction the test tables
    public static boolean useTestTables = false;

    // Private constructor to give singleton pattern
    private Database() {}

    /**
     * Create a database connection
     * 
     * @param host the host site for the database
     * @param port the port for the database
     * @param path the path to get to our instance
     * @param user the user to access the database
     * @param pass the password to access the database
     * @return a database instance
     */
    static Database getDatabase(String host, String port, String path, String user, String pass) {
        if (path == null || "".equals(path)) {
            path = "/";
        }

        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            String dbUrl = "jdbc:postgresql://" + host + ':' + port + path;
            Connection conn = DriverManager.getConnection(dbUrl, user, pass);
            if (conn == null) {
                System.err.println("Error: DriverManager.getConnection() returned a null object");
                return null;
            }
            db.mConnection = conn;
        } catch (SQLException e) {
            System.err.println("Error: DriverManager.getConnection() threw a SQLException");
            e.printStackTrace();
            return null;
        }

        db = db.createPreparedStatements();
        return db;
    }

    /**
     * Get a database instance from a database url and port
     * 
     * @param db_url       the database url
     * @param port_default the port
     * @return a database instance
     */
    static Database getDatabase(String db_url, String port_default) {
        try {
            URI dbUri = new URI(db_url);
            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String host = dbUri.getHost();
            String path = dbUri.getPath();
            String port = dbUri.getPort() == -1 ? port_default : Integer.toString(dbUri.getPort());

            return getDatabase(host, port, path, username, password);
        } catch (URISyntaxException s) {
            System.out.println("URI Syntax Error");
            return null;
        }
    }

    /**
     * Create a database instance with loaded sql statements
     * 
     * @return the modified database instance
     */
    private Database createPreparedStatements() {
        // Attempt to create all of our prepared statements. If any of these
        // fail, the whole getDatabase() call should fail
        try {
            SafeStatement.TableMapping tblMessages = new SafeStatement.TableMapping("tblMessages", "tblMessages");
            SafeStatement.TableMapping tblComments = new SafeStatement.TableMapping("tblComments", "tblComments");
            SafeStatement.TableMapping tblUsers = new SafeStatement.TableMapping("tblUsers", "tblUsers");
            SafeStatement.TableMapping tblLikes = new SafeStatement.TableMapping("tblLikes", "tblLikes");
            if (useTestTables) {
                tblMessages = new SafeStatement.TableMapping("tblMessages", "testTblMessages");
                tblComments = new SafeStatement.TableMapping("tblComments", "testTblComments");
                tblUsers = new SafeStatement.TableMapping("tblUsers", "testTblUsers");
                tblLikes = new SafeStatement.TableMapping("tblLikes", "testTblLikes");
            }

            // Set the connection
            SafeStatement.conn = mConnection;
            ArrayList<SafeStatement.TableMapping> mapping = new ArrayList<SafeStatement.TableMapping>();
            mapping.add(tblMessages);
            mapping.add(tblComments);
            mapping.add(tblUsers);
            mapping.add(tblLikes);
            SafeStatement.setTableMapping(mapping);

            messagesDatabaseInstance = new SQLMessage(tblMessages);
            likesDatabaseInstance = new SQLLike(tblLikes);
            commentsDatabaseInstance = new SQLComment(tblComments);
            usersDatabaseInstance = new SQLUser(tblUsers);


            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table
            // creation/deletion, so multiple executions will cause an exception
            createTables = mConnection.prepareStatement(
                    messagesTable().getCreateTable() + ";" + commentsTable().getCreateTable() + ";" + usersTable().getCreateTable() + ";" + likesTable().getCreateTable());
            dropTables = mConnection.prepareStatement("DROP TABLE " + tblMessages.tableName + "; DROP TABLE "
                    + tblComments.tableName + "; DROP TABLE " + tblUsers.tableName + "; DROP TABLE " + tblLikes.tableName);
        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            disconnect();
        }
        return this;
    }

    /**
     * Disconnect from the database connection
     */
    boolean disconnect() {
        if (mConnection == null) {
            System.err.println("Unable to close connection: Connection was null");
            return false;
        }
        try {
            mConnection.close();
        } catch (SQLException e) {
            System.err.println("Error: Connection.close() threw a SQLException");
            e.printStackTrace();
            mConnection = null;
            return false;
        }
        mConnection = null;
        return true;
    }

    /**
     * Create a table
     * 
     * @return true if the execution was successful
     */
    boolean createTable() {
        try {
            createTables.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a table
     * 
     * @return the status of the execution
     */
    boolean dropTable() {
        try {
            dropTables.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Couldn't delete tables.");
            return false;
        }
    }
}
