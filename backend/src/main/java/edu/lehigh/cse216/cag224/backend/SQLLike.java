package edu.lehigh.cse216.cag224.backend;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLLike {
    // Vote stuff
    private String createLikesTable;
    private SafeStatement updateVoteStatus;
    private SafeStatement getVoteStatus;
    private SafeStatement insertVote;
    private SafeStatement sumVotes;

    /**
     * Construct an accessor for the tbl likes instance
     * @param tblLikes the table mapping just for creating the statement
     * @throws SQLException if their is an error creating the safestatements
     */
    public SQLLike(SafeStatement.TableMapping tblLikes) throws SQLException {
        // Like operations
        createLikesTable = "CREATE TABLE " + tblLikes.tableName + " (voteID SERIAL PRIMARY KEY, userID INTEGER NOT NULL, messageID INTEGER NOT NULL, voteState INT NOT NULL)";
        getVoteStatus = new SafeStatement("SELECT voteState FROM tblLikes WHERE userID = {int,userID} AND messageID = {int,messageID}", true);
        insertVote = new SafeStatement("INSERT INTO tblLikes VALUES (default, {int,userID}, {int,messageID}, {int,voteState})", false);
        sumVotes = new SafeStatement("SELECT SUM(voteState) FROM tblLikes WHERE messageID = {int,messageID}", true);
        updateVoteStatus = new SafeStatement("UPDATE tblLikes SET voteState = {int,voteState} WHERE userID = {int,userID} AND messageID = {int,messageID}",false);
    }

    /**
     * Get the string for creating the table
     * @return the string for creating the table
     */
    String getCreateTable(){
        return createLikesTable;
    }

     /**
     * This will first call the select query to get the current like amount, then
     * increment that and use the update statement.
     * 
     * @param id of the message you want to add a like to
     * @return the new total like count after the votes
     */
    Responses.LikeStates voteOnMessage(int messageID, int userID, int likeButton)
            throws BackendException, SQLException {
        Responses.LikeStates response = new Responses.LikeStates(0, 0);
        if (likeButton != 1 && likeButton != -1)
            throw new BackendException("Invalid button status");
        ResultSet rs = getVoteStatus.setInt("userID", userID).setInt("messageID", messageID).executeQuery();
        int oldLikeStatus = 0;
        if (!rs.next()) {
            // If there is no data, insert it
            insertVote.setInt("userID", userID).setInt("messageID", messageID).setInt("voteState", likeButton).executeQuery();
            response.newLikeStatus = likeButton;
        } else {
            oldLikeStatus = rs.getInt(1);
            if (oldLikeStatus == likeButton) {
                // Update the table as empty
                updateVoteStatus.setInt("voteState", 0).setInt("userID", userID).setInt("messageID", messageID).executeQuery();
                response.newLikeStatus = 0;
            } else {
                // Update the table
                updateVoteStatus.setInt("voteState", likeButton).setInt("userID", userID)
                        .setInt("messageID", messageID).executeQuery();
                response.newLikeStatus = likeButton;
            }
        }

        // Calculate new total likes
        ResultSet sum = sumVotes.setInt("messageID", messageID).executeQuery();
        if (sum.next())
            response.newTotalLikeCount = sum.getInt(1);
        rs.close();
        sum.close();
        return response;
    }
}
