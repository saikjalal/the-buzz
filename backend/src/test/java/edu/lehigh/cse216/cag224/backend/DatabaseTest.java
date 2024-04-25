package edu.lehigh.cse216.cag224.backend;

import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.rubyeye.xmemcached.MemcachedClient;

public class DatabaseTest extends TestCase {
    private Database db;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DatabaseTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(DatabaseTest.class);
    }

    protected void tearDown() throws Exception {
        // Try to drop the tables at the end
        db.dropTable(); // comment this line out if you want to see the output of the tests left in the
                        // database
        db.disconnect();
    }

    public void testStatements() throws BackendException {
        try {
            MemcachedClient mc = App.memcacheBuilder();

            Database.useTestTables = true;
            db = Database.getDatabase(System.getenv("DATABASE_URL"), "5432");

            // Drop old testing table
            db.dropTable();
            // Add testing tables back
            boolean result = db.createTable();
            assertTrue(result);

            // Test message insert
            assertTrue(db.messagesTable().insertMessage(0, "Message 1", null));

            // Test message get
            Message message = db.messagesTable().getOneMessage(1, 1, mc);
            assertTrue(message.mContent.equals("Message 1") && message.mId == 1 && message.mComments.size() == 0);
            assertTrue(message.myLikeStatus == 0 && message.mLikes == 0);

            // Test comment insert
            result = db.commentsTable().insertComment(5, 1, "Comment 1", null);
            assertTrue(result);

            // Check updating comments
            int cmid = db.messagesTable().getOneMessage(1, 5, mc).mComments.get(0).commentID;
            assertTrue(db.commentsTable().updateComment(5, cmid, "Comment 2", null)); // can update with correct user id
            assertTrue(db.commentsTable().updateComment(2, cmid, "Comment 3", null)); // cant update with wrong userid. But query returns true
            Message m = db.messagesTable().getOneMessage(1, 5, mc);
            Comment c = m.mComments.get(0);
            assertTrue(c.comment.equals("Comment 2"));
            assertTrue(c.messageID == 1);
            assertTrue(c.userID == 5);
            assertTrue(c.commentID == cmid);

            // Check deleting comments
            assertTrue(db.commentsTable().deleteComment(2, cmid));
            assertTrue(db.messagesTable().getOneMessage(1, 1, mc).mComments.size() == 1); // shouldn't delete because wrong user id

            assertTrue(db.commentsTable().deleteComment(5, cmid));
            assertTrue(db.messagesTable().getOneMessage(1, 1, mc).mComments.size() == 0); // should be deleted now

            String fileString = "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";
            db.messagesTable().insertMessage(1, "test message", fileString);
            ArrayList<Message> messages = db.messagesTable().getAllMessages(1);
            String mFile = db.messagesTable().getOneMessage((messages.get(0).mId),1, mc).mFile;
            System.out.println(mFile);
            db.commentsTable().insertComment(1, (messages.get(0).mId), "comment with file attached", fileString);
            messages = db.messagesTable().getAllMessages(1);
            String cFile = db.messagesTable().getOneMessage((messages.get(0).mId), 1, mc).mComments.get(0).file;
            System.out.println(cFile);
            // Check update message
            // assertTrue(db.messagesTable().updateMessage(m.mId, "New Message"));
            // assertTrue(db.messagesTable().getOneMessage(1, 1).mContent.equals("New Message"));

            // Test vote insert
            try {
                // First vote is inserted
                Responses.LikeStates states = db.likesTable().voteOnMessage(1, 1, 1);
                assertTrue(states.newLikeStatus == 1 && states.newTotalLikeCount == 1);

                // Second user makes a downvote
                states = db.likesTable().voteOnMessage(1, 2, -1);
                assertTrue(states.newLikeStatus == -1 && states.newTotalLikeCount == 0);

                // Second user makes another downvote and status goes to neutral
                states = db.likesTable().voteOnMessage(1, 2, -1);
                assertTrue(states.newLikeStatus == 0 && states.newTotalLikeCount == 1);

                // First user makes a downvote and we get a big swing
                states = db.likesTable().voteOnMessage(1, 1, -1);
                assertTrue(states.newLikeStatus == -1 && states.newTotalLikeCount == -1);

                // Second user makes a downvote so we end up as neg 2
                states = db.likesTable().voteOnMessage(1, 2, -1);
                assertTrue(states.newLikeStatus == -1 && states.newTotalLikeCount == -2);
            } catch (BackendException e) {
                e.printStackTrace();
                assertTrue(false);
            }

            // Delete the message and insert two new ones
            assertTrue(db.messagesTable().deleteMessage(1));
            assertTrue(db.messagesTable().insertMessage(10, "Message 2", null));
            assertTrue(db.messagesTable().insertMessage(11, "Message 3", null));

            messages = db.messagesTable().getAllMessages(1);
            assertTrue(messages.size() == 3);
            assertTrue(messages.get(0).userID == 11); // check if we are sorting by latest added first
            Message testMessage1 = db.messagesTable().getOneMessage(messages.get(0).mId, 11, mc);
            assertTrue(testMessage1.mComments.size() == 0);
            assertTrue(testMessage1.mLikes == 0);
            assertTrue(testMessage1.myLikeStatus == 0);
            /* previous tests
            assertTrue(messages.get(0).mComments.size() == 0);
            assertTrue(messages.get(0).mLikes == 0);
            assertTrue(messages.get(0).myLikeStatus == 0);
            */

            // Add comment, give the message a like as well
            db.commentsTable().insertComment(1, messages.get(1).mId, "Comment on user 11 post", null);
            db.likesTable().voteOnMessage(messages.get(1).mId, 1, 1);

            // Get messages again and check results
            messages = db.messagesTable().getAllMessages(1);
            assertTrue(messages.size() == 3);
            assertTrue(messages.get(0).userID == 11); // check if we are sorting by latest added first
            testMessage1 = db.messagesTable().getOneMessage(messages.get(0).mId, 1, mc);
            assertTrue(testMessage1.mComments.size() == 0);
            assertTrue(messages.get(0).mLikes == 0);
            assertTrue(messages.get(0).myLikeStatus == 0);
            assertTrue(messages.get(1).userID == 10);
            Message testMessage2 = db.messagesTable().getOneMessage(messages.get(1).mId, 1, mc);
            assertTrue(testMessage2.mComments.size() == 1);
            assertTrue(messages.get(1).mLikes == 1);
            assertTrue(messages.get(1).myLikeStatus == 1);

            // Test user profile functions
            User newUsr = db.usersTable().tryCreateUser("email", "name");
            assertTrue(newUsr != null);
            Integer uid = newUsr.uid;
            assertTrue(uid != null);
            Integer sameUser = db.usersTable().tryCreateUser("email", "name2").uid;
            assertTrue(uid == sameUser);

            // Test get/insert
            User u = db.usersTable().getUser(uid, uid);
            assertTrue(u != null);
            assertEquals(u.name, "name");
            assertEquals(u.email, "email");
            assertEquals(u.gender_identity, "");
            assertEquals(u.sexualOrientation, "");
            assertEquals(u.bio, "");
            assertTrue(u.uid == 1);
            // Test edit
            boolean success = db.usersTable().updateUser(1, new User(1, "Username!", "Email", "G_I", "S_I", "bio", false));
            assertTrue(success);
            // User gets own profile
            u = db.usersTable().getUser(1, 1);
            assertTrue(u != null);
            assertEquals(u.name, "Username!");
            assertEquals(u.email, "Email");
            assertEquals(u.gender_identity, "G_I");
            assertEquals(u.sexualOrientation, "S_I");
            assertEquals(u.bio, "bio");
            assertTrue(u.uid == 1);
            // User gets a different profile
            u = db.usersTable().getUser(0, 1);
            assertTrue(u != null);
            assertEquals(u.name, "Username!");
            assertEquals(u.email, "Email");
            assertEquals(u.gender_identity, "");
            assertEquals(u.sexualOrientation, "");
            assertEquals(u.bio, "bio");
            assertTrue(u.uid == 1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
            System.out.println("Found SQL Exception unexpectedly");
            assertTrue(false);
        }
    }
}
