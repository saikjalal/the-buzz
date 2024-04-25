package edu.lehigh.cse216.cag224.backend;

abstract class Responses {
    public static class StructuredResponse {
        public String mStatus;
        public String mMessage;
        public Object mData;
    
        /**
         * A structured response that we can start to expect as always
         * @param status true if there is no error, false if there is an error
         * @param message the error message
         * @param data the data to return (will be processed into json)
         */
        public StructuredResponse(boolean status, String message, Object data){
            mStatus = status ? "ok" : "error";
            mMessage = message;
            mData = data;
        }
    }

    
    public static class LikeStates {
        public int newTotalLikeCount;
        public int newLikeStatus;

        /**
         * The like states to return as a response
         * @param newLikeCount
         * @param newLikeStatus
         */
        LikeStates(int newLikeCount, int newLikeStatus){
            this.newTotalLikeCount = newLikeCount;
            this.newLikeStatus = newLikeStatus;
        }
    }

    public static class Session {
        int userID;
        String sessionToken;
    
        /**
         * Create a session descriptor to return to the user on authenticate
         * @param userID the auth-ed user's ID
         * @param sessionToken the auth-ed user's session token, to be used on all future communication
         */
        Session(int userID, String sessionToken){
            this.userID = userID;
            this.sessionToken = sessionToken;
        }
    }
}


