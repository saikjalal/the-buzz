package edu.lehigh.cse216.cag224.backend;

interface RequestTemplate {
    /**
     * Check the validity of the input
     * 
     * @throws BackendException if the token couldn't be verified
     */
    public void verify() throws BackendException;
}

// A abstract class to contain all the requests possible. Templates for
// processing json
abstract class Requests {
    // Request for session token
    public static class OAuthToken implements RequestTemplate {
        public String oauth_token;

        /**
         * Check the validity of a token
         * 
         * @throws BackendException if the token couldn't be verified
         */
        public void verify() throws BackendException {
            if (oauth_token == null)
                throw new BackendException("Field oauth_token was found to be null. Make sure this field was provided");
        }
    }

    // New message to add to the database
    public static class NewMessage implements RequestTemplate {
        public String mMessage;
        public String mFile;

        /**
         * Check the validity of the input
         * 
         * @throws BackendException if the token couldn't be verified
         */
        public void verify() throws BackendException {
            if (mMessage == null)
                throw new BackendException("Field mMessage was found to be null. Make sure this field was provided");
        }
    }

    public static class NewComment implements RequestTemplate {
        public Integer messageId;
        public String comment;
        public String mFile;

        /**
         * Check the validity of the input
         * 
         * @throws BackendException if the token couldn't be verified
         */
        public void verify() throws BackendException {
            if (messageId == null)
                throw new BackendException("Field messageId was found to be null. Make sure this field was provided");
            if (comment == null)
                throw new BackendException("Field comment was found to be null. Make sure this field was provided");
        }
    }

    public static class FullUserProfile implements RequestTemplate {
        public String name;
        public String gender_identity;
        public String sexualOrientation;
        public String bio;

        /**
         * Check the validity of the input
         * 
         * @throws BackendException if the token couldn't be verified
         */
        public void verify() throws BackendException {
            if (name == null)
                throw new BackendException("Field name was found to be null. Make sure this field was provided");
            if (gender_identity == null)
                throw new BackendException(
                        "Field gender_identity was found to be null. Make sure this field was provided");
            if (sexualOrientation == null)
                throw new BackendException(
                        "Field sexualOrientation was found to be null. Make sure this field was provided");
            if (bio == null)
                throw new BackendException("Field bio was found to be null. Make sure this field was provided");
        }
    }
}
