package edu.lehigh.cse216.cag224.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SQLMessage {
    // Message CRUD
    private String createMessagesTable;
    private SafeStatement getAllMessages;
    private SafeStatement getOneMessage;
    private SafeStatement deleteMessage;
    private SafeStatement insertMessage;
    private SafeStatement updateMessage;


    /**
     * Construct an accessor for the tbl messages instance
     * @param tblMessages the table mapping just for creating the statement
     * @throws SQLException if their is an error creating the safestatements
     */
    public SQLMessage(SafeStatement.TableMapping tblMessages) throws SQLException {
        // Message operations
        createMessagesTable = "CREATE TABLE " + tblMessages.tableName
                + " (messageID SERIAL PRIMARY KEY, userID INTEGER NOT NULL, message VARCHAR(1024) NOT NULL, createdDate DATE DEFAULT CURRENT_TIMESTAMP, fileID VARCHAR(1024))";
        deleteMessage = new SafeStatement(
                "DELETE FROM tblMessages WHERE messageID = {int,messageID1}; DELETE FROM tblComments WHERE messageID = {int,messageID2}; DELETE FROM tblLikes WHERE messageID = {int,messageID3}",
                false);
        insertMessage = new SafeStatement(
                "INSERT INTO tblMessages VALUES (default, {int,userID},{str,message}, default, {str,fileID})", false);
        /* old getAllMessages
        getAllMessages = new SafeStatement(
                "SELECT ms.message AS message,ms.messageid AS mid,ms.userid AS muid,cm.comment AS comment,cm.commentid AS cid,cm.userid AS cuid,voteState,voteCount FROM tblMessages AS ms LEFT OUTER JOIN tblComments AS cm ON cm.messageid=ms.messageid LEFT OUTER JOIN(SELECT lk.messageid, SUM(lk.votestate) AS voteCount FROM tblLikes AS lk GROUP BY messageid)AS voter ON voter.messageid=ms.messageid LEFT OUTER JOIN(SELECT lk.messageid,lk.userid,lk.votestate AS voteState FROM tblLikes AS lk WHERE lk.userid={int,userID})AS myvote ON myvote.messageid=ms.messageid",
                true);
        */
        // new getAllMessages taht doesn't include
        getAllMessages = new SafeStatement(
                "SELECT ms.message AS message,ms.messageid AS mid,ms.userid AS muid,voteState,voteCount FROM tblMessages AS ms LEFT OUTER JOIN(SELECT lk.messageid,SUM(lk.votestate)AS voteCount FROM tblLikes AS lk GROUP BY messageid)AS voter ON voter.messageid=ms.messageid LEFT OUTER JOIN(SELECT lk.messageid,lk.userid,lk.votestate AS voteState FROM tblLikes AS lk WHERE lk.userid={int,userID})AS myvote ON myvote.messageid=ms.messageid",
                true);

        // recommended to view this sql statement in table+ to avoid trying to read this
        // long statement
        getOneMessage = new SafeStatement(
            "SELECT ms.message AS message, ms.messageid AS mid, ms.userid AS muid, ms.fileid AS mfile, cm.comment AS COMMENT, cm.commentid AS cid, cm.userid AS cuid, cm.cfileid AS cfileid, voteState, voteCount FROM tblMessages AS ms LEFT OUTER JOIN tblComments AS cm ON cm.messageid = ms.messageid LEFT OUTER JOIN (SELECT lk.messageid, SUM(lk.votestate) AS voteCount FROM tblLikes AS lk GROUP BY messageid) AS voter ON voter.messageid = ms.messageid LEFT OUTER JOIN (SELECT lk.messageid, lk.userid, lk.votestate AS voteState FROM tblLikes AS lk WHERE lk.userid = {int,userID}) AS myvote ON myvote.messageid = ms.messageid WHERE ms.messageid = {int,messageID}",
                true);
        updateMessage = new SafeStatement(
                "UPDATE tblMessages SET message = {str,message}, SET fileID = {str,fileID} WHERE messageID = {int,messageID}",
                false);
    }
    
    /**
     * Get the string for creating the table
     * @return the string for creating the table
     */
    String getCreateTable(){
        return createMessagesTable;
    }

    /**
     * Insert a message into the database
     * 
     * @param message The message you want to post in a string.
     * @return if the insert was successful
     */
    boolean insertMessage(int userID, String message, String mFile) throws SQLException {
        String fileID = null;
        if (mFile != null && mFile != "") {
            fileID = uploadFile(mFile);
        }
        insertMessage.setInt("userID", userID).setString("message", message).setString("fileID", fileID).executeQuery();
        return true;
    }

    /**
     * Sends a select sql statment that collects everything from the database and
     * places them
     * 
     * @return
     */
    ArrayList<Message> getAllMessages(int userID) throws SQLException {
        ArrayList<Message> response = new ArrayList<Message>();
        ResultSet result = getAllMessages.setInt("userID", userID).executeQuery();
        while (result.next()) {
            Message m = Message.factory(result, null, null);
            response.add(m);
        }
        result.close();
        // sort by latest messages added first
        Collections.sort(response, Collections.reverseOrder(null));
        return response;
    }
    /* Ethan's getAllMessages method
    ArrayList<Message> getAllMessages(int userID) throws SQLException {
        ArrayList<Message> response = new ArrayList<Message>();
        ResultSet result = getAllMessages.setInt("userID", userID).executeQuery();
        HashMap<Integer, Message> map = new HashMap<Integer, Message>();
        while (result.next()) {
            Integer id = result.getInt("mid");
            Message m = map.get(id);
            if (m == null) {
                ArrayList<Comment> comments = new ArrayList<Comment>();
                m = Message.factory(result, comments);
                map.put(id, m);
                if (result.getString("comment") == null)
                    continue;
                m.mComments.add(Comment.factory(result));
            } else {
                if (result.getString("comment") == null)
                    continue;
                m.mComments.add(Comment.factory(result));
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
    }
    */
    
    /**
     * Grabs the given id, inputs them into the statement to send to the database
     * 
     * @param messageID of the message that we want to get
     * @param userID    the user's ID (only to get like status. Won't affect other
     *                  output)
     * @return the data row object containing the info for the selected row
     */
    synchronized Message getOneMessage(int messageID, int userID, MemcachedClient mc) throws SQLException {
        Message res = null;
        ResultSet result = getOneMessage.setInt("messageID", messageID).setInt("userID", userID).executeQuery();
        ArrayList<Comment> comments = new ArrayList<Comment>();
        if (result.next()) {
            String fileString = null;
            String fileID = result.getString("mfile");
            if (fileID != null) {
                try {
                    fileString = mc.get(fileID);
                } catch (TimeoutException | InterruptedException | MemcachedException e) {
                    e.printStackTrace();
                }

                if (fileString == null) {
                    // Set a key-value for key: fileID, value: base64 string
                    try {
                        fileString = downloadFile(fileID);
                        // Add it to the cache here
                        mc.set(fileID, 5, fileString);
                    } catch (IOException | TimeoutException | InterruptedException | MemcachedException e) {
                        e.printStackTrace();
                    }
                }
            }
            res = Message.factory(result, comments, fileString);
        } else {
            return null;
        }
        do {
            if (result.getString("comment") == null)
                continue;
            String commentFileID = result.getString("cfileID");
            String commentFileString = null;
            if (commentFileID != null) {
                try {
                    commentFileString = downloadFile(commentFileID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Comment comment = Comment.factory(result, commentFileString);
            comments.add(comment);
        } while (result.next());
        result.close();
        return res;
    }

    /**
     * Delete message
     * 
     * @param id of the message you want to delete
     * @return a response from the database if successful
     */
    boolean deleteMessage(int id) throws SQLException {
        // specifiying the message id for all three tables
        deleteMessage.setInt("messageID1", id).setInt("messageID2", id).setInt("messageID3", id).executeQuery();
        return true;
    }

    /**
     * Update message - carlos if you look at this i have not updated it
     * 
     * @param id         of the message you want to delete
     * @param newMessage the new message we want
     * @return if query was successful
     */
    boolean updateMessage(int id, String newMessage) throws BackendException, SQLException {
        updateMessage.setString("message", newMessage).setInt("messageID", id).executeQuery();
        return true;
    }

    /**
     * Upload new file to google drive
     *
     * @return i dont even know yet
     */
    public static String uploadFile(String fileString) {
        byte[] decodedImg = Base64.getDecoder().decode(fileString);
        Path destinationFile = Paths.get("./", "myImage.jpg");
        try {
            Files.write(destinationFile, decodedImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            // Load pre-authorized user credentials from the environment.
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            // Build a new authorized API client service.
            Drive service = new Drive.Builder(new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    requestInitializer)
                    .setApplicationName("Drive samples")
                    .build();
            // Upload file photo.jpg on drive.
            File fileMetadata = new File();
            fileMetadata.setName("photo.jpg");
            // File's content.
            java.io.File filePath = new java.io.File("myImage.jpg");
            // Specify media type and file-path for file.
            FileContent mediaContent = new FileContent("image/jpeg", filePath);
            File file = service.files().create(fileMetadata, mediaContent).setFields("id").execute();
            System.out.println("File ID: " + file.getId());
            File f = service.files().get(file.getId()).execute();
            // f.get()
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to upload file: " + e.getDetails());
        } catch (IOException e) {
            System.err.println("Unable to upload file: " + e.getMessage());
        }
        return ("Failed");
    }

    /**
   * Download a Document file in PDF format.
   *
   * @param realFileId file ID of any workspace document format file.
   * @throws IOException if service account credentials file not found.
   */
    public static String downloadFile(String realFileId) throws IOException {
        /* Load pre-authorized user credentials from the environment.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),GsonFactory.getDefaultInstance(),requestInitializer).setApplicationName("Drive samples").build();

        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            service.files().get(realFileId).executeMediaAndDownloadTo(outputStream);
            ByteArrayOutputStream modStream = (ByteArrayOutputStream) outputStream;

            InputStream stream = new ByteArrayInputStream(modStream.toByteArray());
            BufferedImage img = ImageIO.read(stream);
            java.io.File f = new java.io.File("output.jpg");
            ImageIO.write(img, "jpg", f);
            byte[] bytes = Files.readAllBytes(f.toPath());

            String fileString = Base64.getEncoder().encodeToString(bytes);
            return fileString;
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to move file: " + e.getDetails());
            throw e;
        }
    }
}

