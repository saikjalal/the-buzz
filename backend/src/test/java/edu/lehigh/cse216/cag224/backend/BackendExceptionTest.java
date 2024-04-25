package edu.lehigh.cse216.cag224.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BackendExceptionTest extends TestCase {
    public BackendExceptionTest(String testName){
        super(testName);
    }

    public static Test suite(){
        return new TestSuite(BackendExceptionTest.class);
    }

    public void testAll(){
        // test try catch for execption
        try {
            throw new BackendException("Error 1");
        } catch (Exception exception){
            System.out.println(exception.getLocalizedMessage());
            assertEquals(exception.getLocalizedMessage(), "Error 1");
        }

        // test try catch for backend exception
        try {
            throw new BackendException("Error 2");
        } catch (BackendException exception){
            System.out.println(exception.getLocalizedMessage());
            assertEquals(exception.getLocalizedMessage(), "Error 2");
        }
    }
}