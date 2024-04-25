package edu.lehigh.cse216.cag224.backend;

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DataRowTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DataRowTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DataRowTest.class);
    }

    /**
     * Ensure that the constructor populates every field of the object it
     * creates
     */
    public void testConstructor() {
        int id = 17;
        int uid = 20;
        int likes = 2;
        String content = "Test Content";
        int myLikeStatus = 100;
        ArrayList<Comment> mComments = new ArrayList<Comment>();
        String fileString = "Test";
        Message d = new Message(id, uid, content, likes, myLikeStatus, mComments, fileString);

        assertTrue(d.mContent.equals(content));
        assertTrue(d.mId == id);
        assertTrue(d.userID == uid);
        assertTrue(d.mLikes == likes);
        assertTrue(d.myLikeStatus == myLikeStatus);
        assertTrue(d.mComments == mComments);
    }
}
