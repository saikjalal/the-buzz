package edu.lehigh.cse216.mfs409.admin;

public class Comment {
    public int commentID;
    public int userID;
    public int messageID;
    public String comment;
    public String cfileID;

    
    Comment(int commentID, int userID, int messageID, String comment){
        this.commentID = commentID;
        this.userID = userID;
        this.messageID = messageID;
        this.comment = comment;
    }
    Comment(int commentID, int userID, int messageID, String comment, String cfileID){
        this.commentID = commentID;
        this.userID = userID;
        this.messageID = messageID;
        this.comment = comment;
        this.cfileID = cfileID;
    }
}