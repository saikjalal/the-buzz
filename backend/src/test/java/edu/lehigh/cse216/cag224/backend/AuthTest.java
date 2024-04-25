package edu.lehigh.cse216.cag224.backend;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AuthTest extends TestCase {
    public AuthTest(String testName){
        super(testName);
    }

    public static Test suite(){
        return new TestSuite(AuthTest.class);
    }

    public void testAll(){
        String email = "email@google.com";
        byte[] byteArray = new byte[32];
        new Random().nextBytes(byteArray);
        String randomSalt = byteArray.toString();
        String timestamp = new Date().toString();
        String digester = email + randomSalt + timestamp;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            BigInteger no = new BigInteger(1, md.digest(digester.getBytes()));
            String sess = no.toString(16);
            System.out.println("Example session token: " + sess);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Auth auth = new Auth("email@google.com", 0, "1");
        assertTrue(auth.email.equals("email@google.com") && auth.userID == 0 && auth.sessionToken.equals("1"));
    }
}

