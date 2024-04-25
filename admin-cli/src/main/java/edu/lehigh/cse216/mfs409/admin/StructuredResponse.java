package edu.lehigh.cse216.mfs409.admin;

abstract class Responses {
    public static class StructuredResponse {
        public String mStatus;
        public String mMessage;
        public Object mData;
    
        public StructuredResponse(String status, String message, Object data){
            mStatus = (status != null) ? status : "invalid";
            mMessage = message;
            mData = data;
        }
    }

    public static class LikeStates {
        public int newTotalLikeCount;
        public int newLikeStatus;
        LikeStates(int newLikeCount, int newLikeStatus){
            this.newTotalLikeCount = newLikeCount;
            this.newLikeStatus = newLikeStatus;
        }
    }
}