package edu.lehigh.cse216.cag224.backend;

import java.sql.SQLException;

public class SQLComment {
    // Comment stuff
    private String createCommentsTable;
    private SafeStatement insertComment;
    private SafeStatement updateComment;
    private SafeStatement deleteComment;

    /**
     * Construct an accessor for the tbl comments instance
     * @param tblComments the table mapping just for creating the statement
     * @throws SQLException if their is an error creating the safestatements
     */
    public SQLComment(SafeStatement.TableMapping tblComments) throws SQLException {
        // Comment operations
        createCommentsTable = "CREATE TABLE " + tblComments.tableName
        + " (commentID SERIAL PRIMARY KEY, userID INTEGER NOT NULL, messageID INTEGER NOT NULL, comment VARCHAR(1024) NOT NULL, createdDate DATE DEFAULT CURRENT_TIMESTAMP, cfileID VARCHAR(1024))";
        insertComment = new SafeStatement("INSERT INTO tblComments VALUES (default, {int,userID}, {int,messageID}, {str,comment}, default, {str,cfileID})", false);
        updateComment = new SafeStatement(
                "UPDATE tblComments SET comment = {str,comment}, cfileID = {str,cfileID} WHERE userID = {int,userID} AND commentID = {int,commentID}",
        false);
        deleteComment = new SafeStatement("DELETE FROM tblComments WHERE userID = {int,userID} AND commentID = {int,commentID}", false);
    }

    /**
     * Get the string for creating the table
     * @return the string for creating the table
     */
    String getCreateTable(){
        return createCommentsTable;
    }

    /**
     * Add a comment into the tables
     * 
     * @return if the query was successful
     * @throws BackendException
     */
    boolean insertComment(int userID, int messageID, String comment, String fileString) throws BackendException, SQLException {
        String fileID = null;
        if (fileString != null  && fileString != "") {
            fileID = SQLMessage.uploadFile(fileString);
        }
        insertComment.setInt("userID", userID).setInt("messageID", messageID).setString("comment", comment).setString("cfileID", fileID).executeQuery();
        return true;
    }

    /**
     * Editing a comment in the tables
     * 
     * @return if the query was successful
     */
    boolean updateComment(int userID, int commentID, String comment, String fileString) throws BackendException, SQLException {
        // comment = ? WHERE userID = ? AND commentID = ?
        String fileID = null;
        if (fileString != null) {
            fileID = SQLMessage.uploadFile(fileString);
        }
        updateComment.setString("comment", comment).setInt("userID", userID).setInt("commentID", commentID).setString("cfileID", fileID)
                .executeQuery();
        return true;
    }

    /**
     * Remove a comment
     * 
     * @return if the query was successful
     */
    boolean deleteComment(int userID, int commentID) throws SQLException {
        deleteComment.setInt("userID", userID).setInt("commentID", commentID).executeQuery();
        return true;
    }
}
