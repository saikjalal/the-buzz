package edu.lehigh.cse216.cag224.backend;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

// A class for all things needed to support authentication
public class Auth implements Serializable {
    String email;
    Integer userID;
    String sessionToken;

    /**
     * Create an Auth object to bundle authentication related information together
     * @param email the user's email
     * @param uid the user's uid in the database
     * @param sessionToken the user's valid sessionToken
     */
    Auth(String email, Integer uid, String sessionToken) {
        this.email = email;
        this.userID = uid;
        this.sessionToken = sessionToken;
    }

    /**
     * A function for generating a session token, using their email
     * @param email the user's email, needed to give randomness to the uid
     * @return the session token
     */
    static String genSessionToken(String email) {
        try {
            // Generate random bytes
            byte[] byteArray = new byte[32];
            new Random().nextBytes(byteArray);
            String randomSalt = byteArray.toString();

            // Generate timestamp
            String timestamp = new Date().toString();

            // Bundle email, salt, and timestamp
            String digester = email + randomSalt + timestamp;

            // Hash it with SHA-512 to get the session token
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-512");
            BigInteger no = new BigInteger(1, md.digest(digester.getBytes()));

            // Return the sessionToken
            return no.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // Return null if we have a no-such-algorithm issue...
        return null;
    }
}
