package edu.lehigh.cse216.cag224.backend;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUser {
    // User stuff
    private String createUsersTable;
    private SafeStatement updateUser;
    private SafeStatement insertUser;
    private SafeStatement getUserByID;
    private SafeStatement getUserByEmail;

    /**
     * Construct an accessor for the tbl users instance
     * @param tblUsers the table mapping just for creating the statement
     * @throws SQLException if their is an error creating the safestatements
     */
    public SQLUser(SafeStatement.TableMapping tblUsers) throws SQLException {
        // User operations
        createUsersTable = "CREATE TABLE " + tblUsers.tableName
        + " (userID SERIAL PRIMARY KEY, name VARCHAR(64) NOT NULL, email VARCHAR(64) UNIQUE NOT NULL, genderIdentity VARCHAR(64), sexualOrientation VARCHAR(64), bio VARCHAR(1024), isBanned BOOLEAN NOT NULL DEFAULT FALSE)";
        getUserByID = new SafeStatement("SELECT * FROM tblUsers WHERE userID = {int,userID}", true);
        getUserByEmail = new SafeStatement("SELECT * FROM tblUsers WHERE email = {str,email}", true);

        insertUser = new SafeStatement("INSERT INTO tblUsers AS tbl VALUES (default, {str,name}, {str,email}, NULL, NULL, NULL, default) RETURNING *", true);
        updateUser = new SafeStatement("UPDATE tblUsers SET name={str,name}, email={str,email}, genderIdentity={str,gi}, sexualOrientation={str,so}, bio={str,bio} WHERE userID = {int,userID}", false);
    }

    /**
     * Get the string for creating the table
     * @return the string for creating the table
     */
    String getCreateTable(){
        return createUsersTable;
    }

        /**
     * Add/Edit a user in the table
     * 
     * @param userID the user id of the request-maker
     * @param user   the user data
     * @return the status of the operation (true == success)
     */
    boolean updateUser(int userID, User user) throws SQLException {
        // setting params
        updateUser.setString("name", user.name).setString("email", user.email).setString("gi", user.gender_identity).setString("so", user.sexualOrientation).setString("bio", user.bio).setInt("userID", userID);
        // executing
        updateUser.executeQuery();
        return true;
    }

    /**
     * Try to create a user if possible.
     * 
     * @param email the email to check on
     * @param name  the name of the user
     * @return if a user exists or one was created
     */
    User tryCreateUser(String email, String name) throws SQLException {
        ResultSet rs = getUserByEmail.setString("email", email).executeQuery();
        if (rs.next()) {
            return User.factory(rs);
        }
        rs.close();
        // Set name=?, email=?
        rs = insertUser.setString("name", name).setString("email", email).executeQuery();
        if (rs.next()) {
            return User.factory(rs);
        }
        rs.close();
        return null;
    }

    /**
     * Get a user from the database
     * 
     * @param myUserId is the id of the requesting user. For hiding what information
     *                 is private
     * @param userID   is the id to get on
     * @return the user object or null
     */
    User getUser(int myUserId, int userID) throws SQLException {
        User user = null;
        ResultSet rs = getUserByID.setInt("userID", userID).executeQuery();
        if (rs.next()) {
            user = User.factory(rs);
        }
        if (myUserId != userID) {
            user.sexualOrientation = "";
            user.gender_identity = "";
            user.isBanned = false;
        }
        rs.close();
        return user;
    }
}
