package edu.lehigh.cse216.cag224.backend;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Comment {
    public int commentID;
    public int userID;
    public int messageID;
    public String comment;
    public String file;
    
    /**
     * A constructor for the comment
     * @param commentID the comment's id
     * @param userID the id of the user who made the comment
     * @param messageID the id of the message this comment was made under
     * @param comment the comment contents as a string
     * @param file the base 64 file string for the comment
     */
    Comment(int commentID, int userID, int messageID, String comment, String file) {
        this.commentID = commentID;
        this.userID = userID;
        this.messageID = messageID;
        this.comment = comment;
        this.file = file;
    }
    
    /**
     * A constructor for the comment
     * @param commentID the comment's id
     * @param userID the id of the user who made the comment
     * @param messageID the id of the message this comment was made under
     * @param comment the comment contents as a string
     */
    Comment(int commentID, int userID, int messageID, String comment){
        this.commentID = commentID;
        this.userID = userID;
        this.messageID = messageID;
        this.comment = comment;
    }

    /**
     * A comment factory to process a result set.
     * @param result the result set with the following parameters expected
     * @param fileString associated with comment (can be null) 
     * <hr> commentID = result.getInt("cid") <br><br> 
     * commentID = result.getInt("cid") <br><br> 
     * userID = result.getInt("cuid") <br><br> 
     * messageID = result.getInt("mid") <br><br> 
     * comment = result.getInt("comment") <br><br> 
     * @return a Comment instance
     * @throws SQLException if there is an issue processing the result set
     */
    public static Comment factory(ResultSet result, String fileString) throws SQLException {
        if (fileString == null) {
            return new Comment(result.getInt("cid"), result.getInt("cuid"), result.getInt("mid"), result.getString("comment"));
        }
        return new Comment(result.getInt("cid"), result.getInt("cuid"), result.getInt("mid"), result.getString("comment"), fileString);
    }
}
