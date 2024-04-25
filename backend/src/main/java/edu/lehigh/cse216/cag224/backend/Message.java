package edu.lehigh.cse216.cag224.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Comparable<Message> {
    public final int mId;
    public final int userID;
    public String mContent;
    public int mLikes;
    public int myLikeStatus;
    public ArrayList<Comment> mComments;
    public final String mCreated;
    public String mFile;

    public void setmFile(String mFile) {
        this.mFile = mFile;
    }

    /**
     * Construct a message to pass back
     * @param id the message's id
     * @param userID the user's id (who posted the message)
     * @param content the message contents
     * @param likes the like amount for the message
     * @param myLikeStatus the user's like status
     * @param mComments the comments to put in the message
     * @param mFile the (optional) 64 bit string file associated with the message
     */
    Message(int id, int userID, String content, int likes, int myLikeStatus, ArrayList<Comment> mComments,
            String mFile) {
        this.mId = id;
        this.userID = userID;
        this.mContent = content;
        this.mLikes = likes;
        this.myLikeStatus = myLikeStatus;
        this.mCreated = (new Date()).toString();
        this.mComments = mComments;
        this.mFile = mFile;
    }
    
    /**
     * Construct a message for getAllMessage (so it doesn't include comments or files)
     * @param id the message's id
     * @param userID the user's id (who posted the message)
     * @param content the message contents
     * @param likes the like amount for the message
     * @param myLikeStatus the user's like status
     */
    Message(int id, int userID, String content, int likes, int myLikeStatus) {
        this.mId = id;
        this.userID = userID;
        this.mContent = content;
        this.mLikes = likes;
        this.myLikeStatus = myLikeStatus;
        this.mCreated = (new Date()).toString();
    }

    /**
     * A message factory to process a result set.
     * @param result the result set with the following parameters expected 
     * @param comments the comments to use in the message factory, can be null for getAllMessages
     * @param fileString the file in base64, can be null for getAllMessages
     * <hr> message ID = result.getInt("mid") <br><br> 
     * userID = result.getInt("muid") <br><br> 
     * message content = result.getInt("message") <br><br> 
     * message like amount = result.getInt("voteCount") <br><br> 
     * user's vote state = result.getInt("voteState") <br><br> 
     * @return a message instance
     * @throws SQLException if there is an issue processing the result set
     */
    public static Message factory(ResultSet result, ArrayList<Comment> comments, String fileString)
            throws SQLException {
        if (comments == null) { // if the comments arraylist is null, then this is being called by getAllMessages, and we should use the constructor without files or comments
            return new Message(result.getInt("mid"), result.getInt("muid"), result.getString("message"),
                    result.getInt("voteCount"), result.getInt("voteState"));
        }
        return new Message(result.getInt("mid"), result.getInt("muid"), result.getString("message"),
                result.getInt("voteCount"), result.getInt("voteState"), comments, fileString);
    }
    
    public int compareTo(Message m2) {
        Integer id1 = (Integer) this.mId;
        Integer id2 = (Integer) m2.mId;
        return id1.compareTo(id2);
    }
}
