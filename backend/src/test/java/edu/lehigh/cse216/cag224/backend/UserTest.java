package edu.lehigh.cse216.cag224.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class UserTest extends TestCase {
    public UserTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(UserTest.class);
    }

    public void testAll() {
        // Testing constructor
        User user = new User(0, "name", "email", "gi", "si", "bio", false);
        assertTrue(user.uid == 0);
        assertEquals(user.name, "name");
        assertEquals(user.email, "email");
        assertEquals(user.gender_identity, "gi");
        assertEquals(user.sexualOrientation, "si");
        assertEquals(user.bio, "bio");
    }
}
