package edu.lehigh.cse216.mfs409.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;


public class Database {
    /**
     * The connection to the database. When there is no connection, it should
     * be null. Otherwise, there is a valid open connection
     */
    private Connection mConnection;

    // Message CRUD
    private PreparedStatement getAllMessages;
    private PreparedStatement getOneMessage;
    private PreparedStatement deleteMessage;
    private PreparedStatement insertMessage;

    // Vote stuff
    private PreparedStatement updateVoteStatus;
    private PreparedStatement getVoteStatus;
    private PreparedStatement insertVote;
    private PreparedStatement sumVotes;

    // Comment stuff
    private PreparedStatement deleteComment;

    // Admin Table Stuff
    private PreparedStatement createTables;
    private PreparedStatement dropTables;

    // marking a user as invalid
    // note: not in backend version of database.java
    private PreparedStatement userInvalid;

    //Marking files invalid
    private PreparedStatement fileInvalid;
    private PreparedStatement commentFileInvalid;

    // filling the tables with test data
    // note: not in backend version of database.java
    private PreparedStatement fillTestDataMessages;
    private PreparedStatement fillTestDataUsers;
    private PreparedStatement fillTestDataComments;
    private PreparedStatement fillTestDataLikes;

    /**
     * The Database constructor is private: we only create Database objects
     * through the getDatabase() method.
     */
    private Database() {
    }

    /**
     * Get a fully-configured connection to the database
     * 
     * @param ip   The IP address of the database server
     * @param port The port on the database server to which connection requests
     *             should be sent
     * @param user The user ID to use when connecting
     * @param pass The password to use when connecting
     * 
     * @return A Database object, or null if we cannot connect properly
     */
    static Database getDatabase(String ip, String port, String user, String pass) {
        // Create an un-configured Database object
        Database db = new Database();

        // Give the Database object a connection, fail if we cannot get one
        try {
            Connection conn = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + port + "/", user, pass);
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
     * Set SQL prepared statements for database
     * 
     * @return Database with newly added prepared statements
     */
    private Database createPreparedStatements() {
        // Attempt to create all of our prepared statements. If any of these
        // fail, the whole getDatabase() call should fail
        try {
            String tblMessages = "tblMessages";
            String tblComments = "tblComments";
            String tblUsers = "tblUsers";
            String tblLikes = "tblLikes";

            // Message operations
            String createMessagesTable = "CREATE TABLE " + tblMessages
                    + " (messageID SERIAL PRIMARY KEY, userID INTEGER NOT NULL, message VARCHAR(1024) NOT NULL, createdDate DATE DEFAULT CURRENT_TIMESTAMP, fileID VARCHAR(1024))";
            this.deleteMessage = this.mConnection
                    .prepareStatement("DELETE FROM " + tblMessages + " WHERE messageID = ?; DELETE FROM " + tblComments
                            + " WHERE messageID = ?; DELETE FROM " + tblLikes + " WHERE messageID = ?");
            this.insertMessage = this.mConnection
                    .prepareStatement("INSERT INTO " + tblMessages + " VALUES (default, ?, ?, default)");
            this.getOneMessage = this.mConnection.prepareStatement(
                    "SELECT ms.message AS message,ms.messageid AS mid,ms.userid AS muid,cm.comment AS comment,cm.commentid AS cid,cm.userid AS cuid,voteState,voteCount FROM "
                            + tblMessages + " AS ms LEFT OUTER JOIN " + tblComments
                            + " AS cm ON cm.messageid=ms.messageid AND ms.messageid=? LEFT OUTER JOIN(SELECT lk.messageid,SUM(lk.votestate)AS voteCount FROM "
                            + tblLikes
                            + " AS lk GROUP BY messageid)AS voter ON voter.messageid=ms.messageid LEFT OUTER JOIN(SELECT lk.messageid,lk.userid,lk.votestate AS voteState FROM "
                            + tblLikes + " AS lk WHERE lk.userid=?)AS myvote ON myvote.messageid=ms.messageid");
            this.getAllMessages = this.mConnection.prepareStatement(
                    "SELECT ms.message AS message,ms.messageid AS mid,ms.userid AS muid,cm.comment AS comment,cm.commentid AS cid,cm.userid AS cuid,voteState,voteCount, ms.fileid AS mfileid, cm.cfileid AS cfileid FROM "
                            + tblMessages + " AS ms LEFT OUTER JOIN " + tblComments
                            + " AS cm ON cm.messageid=ms.messageid LEFT OUTER JOIN(SELECT lk.messageid,SUM(lk.votestate)AS voteCount FROM "
                            + tblLikes
                            + " AS lk GROUP BY messageid)AS voter ON voter.messageid=ms.messageid LEFT OUTER JOIN(SELECT lk.messageid,lk.userid,lk.votestate AS voteState FROM "
                            + tblLikes + " AS lk WHERE lk.userid=?)AS myvote ON myvote.messageid=ms.messageid");

            // Like operations
            String createLikesTable = "CREATE TABLE " + tblLikes
                    + " (voteID SERIAL PRIMARY KEY, userID INTEGER NOT NULL, messageID INTEGER NOT NULL, voteState INT NOT NULL)";
            this.getVoteStatus = this.mConnection
                    .prepareStatement("SELECT voteState FROM " + tblLikes + " WHERE userID = ? AND messageID = ?");
            this.insertVote = this.mConnection
                    .prepareStatement("INSERT INTO " + tblLikes + " VALUES (default, ?, ?, ?)");
            this.sumVotes = this.mConnection
                    .prepareStatement("SELECT SUM(voteState) FROM " + tblLikes + " WHERE messageID = ?");
            this.updateVoteStatus = this.mConnection
                    .prepareStatement("UPDATE " + tblLikes + " SET voteState = ? WHERE userID = ? AND messageID = ?");

            // Comment operations
            String createCommentsTable = "CREATE TABLE " + tblComments
                    + " (commentID SERIAL PRIMARY KEY, userID INTEGER NOT NULL, messageID INTEGER NOT NULL, comment VARCHAR(1024) NOT NULL, createdDate DATE DEFAULT CURRENT_TIMESTAMP, cfileID VARCHAR(1024))";
            this.deleteComment = this.mConnection
                    .prepareStatement("DELETE FROM " + tblComments + " WHERE userID = ? AND commentID = ?");

            // User operations
            String createUsersTable = "CREATE TABLE " + tblUsers
                    + " (userID SERIAL PRIMARY KEY, name VARCHAR(64) NOT NULL, email VARCHAR(64) NOT NULL, genderIdentity VARCHAR(64) NOT NULL, sexualOrientation VARCHAR(64) NOT NULL, bio VARCHAR(1024) NOT NULL, isBanned BOOLEAN NOT NULL DEFAULT FALSE)";
            this.userInvalid = this.mConnection
                    .prepareStatement("UPDATE " + tblUsers + " SET isBanned = ? WHERE userID = ?");

            //Files operations
            this.fileInvalid = this.mConnection
                    .prepareStatement("UPDATE " + tblMessages + " SET fileID = NULL WHERE messageID = ?");
            this.commentFileInvalid = this.mConnection
                    .prepareStatement("UPDATE " + tblComments + " SET cfileID = NULL WHERE commentID = ?");

            // Note: no "IF NOT EXISTS" or "IF EXISTS" checks on table
            // creation/deletion, so multiple executions will cause an exception
            this.createTables = this.mConnection.prepareStatement(
                    createMessagesTable + ";" + createCommentsTable + ";" + createUsersTable + ";" + createLikesTable);
            this.dropTables = this.mConnection.prepareStatement("DROP TABLE " + tblMessages + "; DROP TABLE "
                    + tblComments + "; DROP TABLE " + tblUsers + "; DROP TABLE " + tblLikes);

            // filling tables with test data
            this.fillTestDataUsers = this.mConnection.prepareStatement("INSERT INTO " + tblUsers
                    + " VALUES (3, 'josie123', 'josie@gmail.com', 'gender iden', 'sexual orientation', 'josie bio', FALSE), (7, 'otheruser45', 'mystuff@gmail.com', 'gender', 'so', 'user bio', FALSE), (10, 'badUser', 'bad@gmail.com', 'gen', 'sxl ortn', 'i am a bad user', TRUE)");
            this.fillTestDataMessages = this.mConnection.prepareStatement("INSERT INTO " + tblMessages
                    + " VALUES (4, 3, 'josie says this with a file attached', default, '1111112222'), (42, 7, 'other user says this', default, null), (50, 3, 'josie says another thing', default,null)");
            this.fillTestDataComments = this.mConnection.prepareStatement("INSERT INTO " + tblComments
                    + " VALUES (1, 3, 42, 'josie commented on other user', default), (17, 7, 4, 'other user commented on josie', default), (20, 3, 4, 'josie commented on her own', default)");
            this.fillTestDataLikes = this.mConnection
                    .prepareStatement("INSERT INTO " + tblLikes
                            + " VALUES (5, 3, 42, 1), (10, 7, 4, -1), (11, 3, 4, -1), (20, 7, 42, 1)");

        } catch (SQLException e) {
            System.err.println("Error creating prepared statement");
            e.printStackTrace();
            this.disconnect();
        }
        return this;
    }

    /**
     * Close the current connection to the database, if one exists.
     * 
     * NB: The connection will always be null after this call, even if an
     * error occurred during the closing operation.
     * 
     * @return True if the connection was cleanly closed, false otherwise
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
     * Insert a message into the database
     * 
     * @param message The message you want to post in a string.
     * @return if the insert was successful
     */
    boolean insertMessage(int userID, String message) {
        try {
            insertMessage.setInt(1, userID);
            insertMessage.setString(2, message);
            insertMessage.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sends a select sql statment that collects all messages from the database and
     * places them
     *
     * @param userID should be -1 since the admin does not have a userID associated
     *               with it
     * @return List of messages with message ID, user ID, content, and total likes
     */
    ArrayList<Message> getAllMessages(int userID) {
        ArrayList<Message> response = new ArrayList<Message>();
        try {
            this.getAllMessages.setInt(1, userID);
            ResultSet result = this.getAllMessages.executeQuery();
            HashMap<Integer, Message> map = new HashMap<Integer, Message>();
            while (result.next()) {
                Integer id = result.getInt("mid");
                Message m = map.get(id);
                if (m == null) {
                    ArrayList<Comment> comments = new ArrayList<Comment>();
                    m = new Message(result.getInt("mid"), result.getInt("muid"), result.getString("message"),
                            result.getInt("voteCount"),
                            result.getInt("voteState"), comments, result.getString("mfileid"));
                    map.put(id, m);
                    if (result.getString("comment") == null)
                        continue;
                    m.mComments.add(new Comment(result.getInt("cid"), result.getInt("cuid"), result.getInt("mid"),
                            result.getString("comment"), result.getString("cfileid")));
                } else {
                    if (result.getString("comment") == null)
                        continue;
                    m.mComments.add(new Comment(result.getInt("cid"), result.getInt("cuid"), result.getInt("mid"),
                            result.getString("comment")));
                }
            }
            result.close();
            Object[] arr = map.keySet().toArray();
            Arrays.sort(arr);
            // Add from the map into the array
            for (int i = arr.length - 1; i >= 0; i--) {
                response.add(map.get(arr[i]));
            }
            return response;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Grabs the given id, inputs them into the statement to send to the database
     * 
     * @param messageID of the message that we want to get
     * @param userID    the user's ID (to get like status)
     * @return the data row object containing the info for the selected row
     */
    Message getOneMessage(int messageID, int userID) {
        Message res = null;
        try {
            this.getOneMessage.setInt(1, messageID);
            this.getOneMessage.setInt(2, userID);
            ResultSet result = this.getOneMessage.executeQuery();
            ArrayList<Comment> comments = new ArrayList<Comment>();
            if (result.next()) {
                res = new Message(result.getInt("mid"), result.getInt("muid"), result.getString("message"),
                        result.getInt("voteCount"),
                        result.getInt("voteState"), comments);
            } else {
                return null;
            }
            do {
                if (result.getString("comment") == null)
                    continue;
                comments.add(new Comment(result.getInt("cid"), result.getInt("cuid"), messageID,
                        result.getString("comment")));
            } while (result.next());
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Delete message
     * 
     * @param id of the message you want to delete
     * @return a response from the database if successful
     */
    boolean deleteMessage(int id) {
        try {
            // specifiying the message id for all three tables
            this.deleteMessage.setInt(1, id);
            this.deleteMessage.setInt(2, id);
            this.deleteMessage.setInt(3, id);
            this.deleteMessage.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This will first call the select query to get the current like amount, then
     * increment that and use the update statement.
     * 
     * @param messageID  of the message you want to add a like to
     * @param userID     of the user whose votes you are updating
     * @param likeButton 1 for upvote, -1 for downvote
     * @return the new total like count after the votes
     */
    Responses.LikeStates voteOnMessage(int messageID, int userID, int likeButton) throws BackendException {
        Responses.LikeStates response = new Responses.LikeStates(0, 0);
        if (likeButton != 1 && likeButton != -1)
            throw new BackendException("Invalid button status");
        try {
            getVoteStatus.setInt(1, userID);
            getVoteStatus.setInt(2, messageID);
            ResultSet rs = getVoteStatus.executeQuery();
            int oldLikeStatus = 0;
            if (!rs.next()) {
                // If there is no data, insert it
                insertVote.setInt(1, userID);
                insertVote.setInt(2, messageID);
                insertVote.setInt(3, likeButton);
                insertVote.executeUpdate();
                response.newLikeStatus = likeButton;
            } else {
                oldLikeStatus = rs.getInt(1);
                if (oldLikeStatus == likeButton) {
                    // Update the table as empty
                    updateVoteStatus.setInt(1, 0);
                    updateVoteStatus.setInt(2, userID);
                    updateVoteStatus.setInt(3, messageID);
                    updateVoteStatus.executeUpdate();
                    response.newLikeStatus = 0;
                } else {
                    // Update the table
                    updateVoteStatus.setInt(1, likeButton);
                    updateVoteStatus.setInt(2, userID);
                    updateVoteStatus.setInt(3, messageID);
                    updateVoteStatus.executeUpdate();
                    response.newLikeStatus = likeButton;
                }
            }

            // Calculate new total likes
            sumVotes.setInt(1, messageID);
            ResultSet sum = sumVotes.executeQuery();
            if (sum.next())
                response.newTotalLikeCount = sum.getInt(1);
            rs.close();
            sum.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    /**
     * Remove a comment
     * 
     * @param userID    of the user whose comment you're deleting
     * @param commentID of the comment you're deleting
     * @return if the query was successful
     */
    boolean deleteComment(int userID, int commentID) {
        // "DELETE FROM " + tblComments + " WHERE userID = ? AND commentID = ?"
        try {
            this.deleteComment.setInt(1, userID);
            this.deleteComment.setInt(2, commentID);
            // Execute
            this.deleteComment.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create all tables
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
     * Delete all tables
     * 
     * @return true if execution was successful
     */
    boolean dropTable() {
        try {
            dropTables.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mark a user as invalid so they cannot log in
     * 
     * @param userID user ID of the user to ban
     * @return true if operation was successful
     */
    boolean invalidateUser(int userID) {
        try {
            // setting isBanned bool to true
            userInvalid.setBoolean(1, true);
            userInvalid.setInt(2, userID);
            userInvalid.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mark a file in a message as invalid so it no longer shows up 
     * @param messageID
     * @return true if operation was successful
     */
    boolean invalidateFile(int messageID) {
        try {
            fileInvalid.setInt(1, messageID);
            fileInvalid.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mark a file in a comment as invalid so it no longer shows up 
     * @param commentiD
     * @return true if operation was successful
     */
    boolean invalidateCommentFile(int commentID) {
        try {
            commentFileInvalid.setInt(1, commentID);
            commentFileInvalid.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fill all the tables with test data
     * 
     * @return true if operation was successful
     */
    boolean fillTestData() {
        try {
            // first create users, then messages, likes, comments
            fillTestDataUsers.execute();
            fillTestDataMessages.execute();
            fillTestDataLikes.execute();
            fillTestDataComments.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}