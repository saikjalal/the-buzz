package edu.lehigh.cse216.cag224.backend;

/**
 * Custom exception to throw on error and catch in Spark routes
 */
public class BackendException extends Exception {
   /**
    * Initializer for an exception intended to be passed around by the backend. In other words, for expected errors
    * @param message the message to return
    */
   BackendException(String message) {
      super(message);
   }
}
