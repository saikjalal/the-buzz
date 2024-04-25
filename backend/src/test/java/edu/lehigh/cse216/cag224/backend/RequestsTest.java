package edu.lehigh.cse216.cag224.backend;

import edu.lehigh.cse216.cag224.backend.Requests.NewMessage;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RequestsTest extends TestCase {
    public RequestsTest(String testName){
        super(testName);
    }

    public static Test suite(){
        return new TestSuite(RequestsTest.class);
    }

    public void testAll(){
        // Testing constructor for new message
        NewMessage res = new Requests.NewMessage();
        res.mMessage = "Hello";
        assertEquals("Hello", res.mMessage);
    }
}