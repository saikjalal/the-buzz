package edu.lehigh.cse216.mfs409.admin;

/**
 * Custom exception to throw on error and catch in Spark routes
 */
public class BackendException extends Exception {
	/**
	 * Custom exception to throw on error and catch in Spark routes
	 */
	BackendException(String message) {
	   super(message);
	}
 }