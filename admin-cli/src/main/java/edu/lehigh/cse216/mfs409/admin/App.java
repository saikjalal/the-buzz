package edu.lehigh.cse216.mfs409.admin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
/**
 * App is our basic admin app. For now, it is a demonstration of the six key
 * operations on a database: connect, insert, update, query, delete, disconnect
 * created CLI java program for db tasks: (re)create tables, drop tables, do
 * maintenance (e.g. delete rows)
 */
public class App {

    /**
     * Print the menu for our program
     * 
     */
    static void menu() {
        // Many of the options were inherited fron the App.java from the tutorial(s)
        System.out.println("Main Menu");
        System.out.println("  [T] Create tables");
        System.out.println("  [R] Remove/drop tables");
        System.out.println("  [F] Fill the tables with test data");
        System.out.println("  [*] See all messages");
        System.out.println("  [-] Invalidate (delete) a message");
        System.out.println("  [+] Insert a new message");
        System.out.println("  [U] Upvote a message");
        System.out.println("  [D] Downvote a message");
        System.out.println("  [B] Invalidate (ban) a user");
        System.out.println("  [M] Invalidate file from message");
        System.out.println("  [C] Invalidate file from comment");
        System.out.println("  [P] Show all files");
        System.out.println("  [X] Delete a file from drive");
        System.out.println("  [A] Auto delete unused files from drive");
        System.out.println("  [?] Help/print menu");
        System.out.println("  [q] Quit");
    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     * 
     * @param in A BufferedReader, for reading from the keyboard
     * 
     * @return The character corresponding to the chosen menu option
     */
    static char prompt(BufferedReader in) {
        // The valid actions:
        String actions = "T R F * - + U D B ? q M C P X";

        // We repeat until a valid single-character option is selected
        while (true) {
            System.out.print("[" + actions + "] :> ");
            String action;
            try {
                action = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if (action.length() != 1)
                continue;
            if (actions.contains(action)) {
                return action.charAt(0);
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     * 
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The string that the user provided. May be "".
     */
    static String getString(BufferedReader in, String message) {
        String s;
        try {
            System.out.print(message + " :> ");
            s = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return s;
    }

    /**
     * Ask the user to enter an integer
     * 
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     * 
     * @return The integer that the user provided. On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        int i = -1;
        try {
            System.out.print(message + " :> ");
            i = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * Create all tables in DB (users, messages, likes, comments)
     * 
     * @param db Database reference
     */
    static void createTables(Database db) {
        if (db.createTable())
            System.out.println("Tables created");
        else
            System.out.println("Unable to create tables");
    }

    /**
     * Drop all tables in DB (users, messages, likes, comments)
     * 
     * @param db Database reference
     */
    static void dropTables(Database db) {
        if (db.dropTable())
            System.out.println("Tables deleted");
        else
            System.out.println("Unable to delete tables");
    }

    /**
     * Perform get all query on DB
     * 
     * @param db Database reference
     */
    static void getAllMessages(Database db) {
        //since admin does not have a userID associated with it, pass in -1
        ArrayList<Message> res = db.getAllMessages(-1);
        if (res == null) {
            System.out.println("Unable to get messages");
            return;
        }
        System.out.printf("%-35sCurrent Database Contents\n", "");
        System.out.printf("%-15s%-50s%-15s%-15s%-15s\n", "messageID", "message content", "posted by", "sum of votes", "fileID");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");

        for (Message rd : res) {
            System.out.printf("%-15d%-50s%-15d%-15d%-15s\n", rd.mId, rd.mContent, rd.userID, rd.mLikes, rd.mfileID);
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
            if (rd.mComments.size() != 0) {
                System.out.printf("\t%-15s%-50s%-15s%-15s\n", "commentID", "comment content", "posted by", "commentFileID");
                for (Comment cm : rd.mComments) {
                    System.out.printf("\t%-15d%-50s%-15d%-15s\n", cm.commentID, cm.comment, cm.userID, cm.cfileID);
                }
            } else {
                System.out.println("\tNo comments");
            }
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------");
        }
    }

    /**
     * Remove message from DB by message ID
     * 
     * @param db Database reference
     * @param in BufferedReader to take in user input
     */
    static void removeMessage(Database db, BufferedReader in) {
        int id = getInt(in, "Enter the message ID for the message to remove: ");
        if (id == -1)
            return;
        if(db.deleteMessage(id))
            System.out.println("Message removed");
        else
            System.out.println("Unable to remove message");
    }

    /**
     * Add message to the DB
     * 
     * @param db Database reference
     * @param in BufferedReader to take in user input
     */
    static void addMessage(Database db, BufferedReader in) {
        String message = getString(in, "Enter the message");
        if (message.equals(""))
            return;
        if(db.insertMessage(0, message))
            System.out.println("Message inserted");
        else
            System.out.println("Unable to insert message");
    }

    /**
     * Upvote a message in the DB by message ID
     * 
     * @param db   Database reference
     * @param in   BufferedReader to take in user
     * @param vote 1 if upvote, -1 if downvote
     */
    static void voteMessage(Database db, BufferedReader in, int vote) {
        int userID = getInt(in, "Enter the user ID for the vote you want to change: ");
        if (userID == -1)
            return;
        int messageID = getInt(in, "Enter the message ID for the vote you want to change: ");
        if (messageID == -1)
            return;

        try {
            db.voteOnMessage(messageID, userID, vote);
            System.out.println("Vote updated");
        } catch (BackendException e) {
            System.out.println("Unable to update vote");
            return;
        }
    }

    /**
     * Fill the tables in the database with test data
     * 
     * @param db Database reference
     * @param in BufferedReader to take in user input
     */
    static void fillTestData(Database db) {
        if(db.fillTestData())
            System.out.println("Tables filled with test data");
        else
            System.out.println("Unable to fill tables with test data");
    }

    /**
     * Mark a user as invalid in the database so they cannot log in
     * 
     * @param db Database reference
     * @param in BufferedReader to take in user input
     */
    static void invalidateUser(Database db, BufferedReader in) {
        int id = getInt(in, "Enter the user ID that you want to ban: ");
        if (id == -1)
            return;
        if(db.invalidateUser(id))
            System.out.println("User marked as banned");
        else
            System.out.println("Unable to mark user as banned");
    }

    /**
     * Remove a file connected to the selected message
     * 
     * @param db Database reference
     * @param in BufferedReader to take in user input
     */
    static void invalidateMessageFile(Database db, BufferedReader in) {
        int msgID = getInt(in, "Enter the message ID of the message with the File you want to remove: ");
        if (msgID == -1)
            return;
        if(db.invalidateFile(msgID))
            System.out.println("File was removed from message");
        else
            System.out.println("Unable to remove file");
    }

     /**
     * Remove a file connected to the selected message
     * 
     * @param db Database reference
     * @param in BufferedReader to take in user input
     */
    static void invalidateCommentFile(Database db, BufferedReader in) {
        int cmntID = getInt(in, "Enter the comment ID of the comment with the File you want to remove: ");
        if (cmntID == -1)
            return;
        if(db.invalidateFile(cmntID))
            System.out.println("File was removed from comment");
        else
            System.out.println("Unable to remove file");
    }

    /**
     * Gets all files from the drive with there names, IDs, and their last modefied data
     * @throws IOException
     */
    public static void searchFile() throws IOException{
        /* Load pre-authorized user credentials from the environment.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),GsonFactory.getDefaultInstance(),requestInitializer).setApplicationName("Drive samples").build();
        try {
            FileList result = service.files().list()
            .setPageSize(20)
            .setFields("nextPageToken, files(id, name, modifiedTime)")
            .execute();
            List<File> files = result.getFiles();
            if (files == null || files.size() == 0) {
                System.out.println("No files found.");
            } else {
                System.out.println("Files:");
                for (File file : files) {
                    System.out.printf("%s (%s) %s\n", file.getName(), file.getId(), file.getModifiedTime());
                }
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to get file: " + e.getDetails());
            throw e;
        }

    }

     /**
     * Deletes all files from the drive that are currently not being used in the messages table.
     * @throws IOException
     */
    public static void autoDeleteFiles(Database db) throws IOException{
        //Get all the messages so that it can compare what files are currently being used
        ArrayList<Message> res = db.getAllMessages(-1);
        if (res == null) {
            System.out.println("Unable to get messages");
            return;
        }

        /* Load pre-authorized user credentials from the environment.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),GsonFactory.getDefaultInstance(),requestInitializer).setApplicationName("Drive samples").build();
        String pageToken = null;
        boolean found = false;
        try {
            FileList result = service.files().list()
            .setFields("nextPageToken, files(id, name, modifiedTime)")
            .setPageToken(pageToken)
            .execute();
            List<File> files = new ArrayList<File>();
            files.addAll(result.getFiles());
            System.out.printf("Number of Files: %d\n", files.size());
            if (files == null || files.size() == 0) {
                System.out.println("No files found.");
            }else{
                for (File file : files) {
                    found = false;
                    String driveFileID = file.getId();
                    for (Message rd : res) {
                        if(driveFileID.equals(rd.mfileID)){
                            found = true;
                        }
                        if (rd.mComments.size() != 0) {
                            for (Comment cm : rd.mComments) {
                                if(driveFileID.equals( cm.cfileID)){
                                    found = true;
                                }
                            }
                        }
                    }
                    if(found == false){
                        service.files().delete(driveFileID).execute();
                    }
                }
            }
            

        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to get file: " + e.getDetails());
            throw e;
        }

    }

    /**
     * Deletes the given file from the drive using the fileID
     * @param in BufferedReader to take in user input
     * @throws IOException
     */
    public static void deleteFile(BufferedReader in) throws IOException{
        String fileID = getString(in, "Enter the fileID");
        if (fileID.equals(""))
            return;
        /* Load pre-authorized user credentials from the environment.*/
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault().createScoped(Arrays.asList(DriveScopes.DRIVE_FILE));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),GsonFactory.getDefaultInstance(),requestInitializer).setApplicationName("Drive samples").build();

        try {
            service.files().delete(fileID).execute();
            System.out.printf("File (%s) was removed from the drive\n", fileID);
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to delete file: " + e.getDetails());
            throw e;
        }

    }



    /**
     * The main routine runs a loop that gets a request from the user and
     * processes it
     * 
     * @param argv Command-line options. Ignored by this program.
     */
    public static void main(String[] argv) {
        // get the Postgres configuration from the environment
        Map<String, String> env = System.getenv();
        String ip = env.get("POSTGRES_IP");
        String port = env.get("POSTGRES_PORT");
        String user = env.get("POSTGRES_USER");
        String pass = env.get("POSTGRES_PASS");

        // Get a fully-configured connection to the database, or exit
        // immediately
        Database db = Database.getDatabase(ip, port, user, pass);
        if (db == null)
            return;

        // Start our basic command-line interpreter:
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        // print menu to start
        menu();

        while (true) {
            // Get the user's request, and do it
            //
            // NB: for better testability, each action should be a separate
            // function call

            char action = prompt(in);
            if (action == '?') {
                menu();
            } else if (action == 'q') {
                break;
            } else if (action == 'T') {
                createTables(db);
            } else if (action == 'R') {
                dropTables(db);
            } else if (action == '*') {
                getAllMessages(db);
            } else if (action == '-') {
                removeMessage(db, in);
            } else if (action == '+') {
                addMessage(db, in);
                // } else if (action == '~') {
                // int id = getInt(in, "Enter the row ID :> ");
                // if (id == -1)
                // continue;
                // String newMessage = getString(in, "Enter the new message");
                // int res = db.updateOne(id, newMessage);
                // if (res == -1)
                // continue;
                // System.out.println(" " + res + " rows updated");
            } else if (action == 'U') {
                voteMessage(db, in, 1);
            } else if (action == 'D') {
                voteMessage(db, in, -1);
            } else if (action == 'F') {
                fillTestData(db);
            } else if (action == 'B') {
                invalidateUser(db, in);
            } else if (action == 'M') {
                invalidateMessageFile(db, in);
            } else if (action == 'C') {
                invalidateCommentFile(db, in);
            } else if (action == 'P') {
                try{
                    searchFile();
                }catch (IOException e) {
                    e.printStackTrace();
                }  
            }else if (action == 'X') {
                try{
                    deleteFile(in);
                }catch (IOException e) {
                    e.printStackTrace();
                }  
            }
            else if (action == 'A') {
                try{
                    autoDeleteFiles(db);
                }catch (IOException e) {
                    e.printStackTrace();
                }  
            }
        }
        // Always remember to disconnect from the database when the program
        // exits
        db.disconnect();
    }
}
