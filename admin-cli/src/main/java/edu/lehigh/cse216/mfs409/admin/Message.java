package edu.lehigh.cse216.mfs409.admin;

import java.util.ArrayList;
import java.util.Date;

public class Message {
	public final int mId;
	public final int userID;
	public String mContent;
	public int mLikes;
	public int myLikeStatus;
	public ArrayList<Comment> mComments;
	public String mfileID;
	public final String mCreated;

	Message(int id, int userID, String content, int likes, int myLikeStatus, ArrayList<Comment> mComments, String fileID) {
		this.mId = id;
		this.userID = userID;
		this.mContent = content;
		this.mLikes = likes;
		this.myLikeStatus = myLikeStatus;
		this.mCreated = (new Date()).toString();
		this.mComments = mComments;
		this.mfileID = fileID;
	}
	Message(int id, int userID, String content, int likes, int myLikeStatus, ArrayList<Comment> mComments) {
		this.mId = id;
		this.userID = userID;
		this.mContent = content;
		this.mLikes = likes;
		this.myLikeStatus = myLikeStatus;
		this.mCreated = (new Date()).toString();
		this.mComments = mComments;
	}
}