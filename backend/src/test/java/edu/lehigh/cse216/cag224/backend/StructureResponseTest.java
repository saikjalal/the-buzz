package edu.lehigh.cse216.cag224.backend;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StructureResponseTest extends TestCase {
    public StructureResponseTest(String testName){
        super(testName);
    }

    public static Test suite(){
        return new TestSuite(StructureResponseTest.class);
    }

    public void testAll(){
        // Testing constructor
        Integer intt = 0;
        Responses.StructuredResponse res = new Responses.StructuredResponse(true, "Message", intt);
        assertEquals("ok", res.mStatus);
        assertEquals("Message", res.mMessage);
        assertEquals(intt, res.mData);

        res = new Responses.StructuredResponse(false, "Message2", intt);
        assertEquals("error", res.mStatus);
        assertEquals("Message2", res.mMessage);
    }
}
