package com.sdsmdg.bookshareapp.BSA.utils;

public class CommonUtilities {
    public static final String goodreads_api_url = "https://www.goodreads.com/";
    public static final String SECRET = "rQzlo9szKNphRLQUjkElZsSYfjyHBnwyHgLfbqHWn0";//#gitignore
    public static final String API_KEY = "OIPSMQ3VvFcBzdZiP61oA";//#gitignore
    public static final String local_books_api_url = "http://bookshare-env.ctnpygtfsy.us-east-2.elasticbeanstalk.com/";
    public static final String currentUserImageUrl =
            "http://bookshare-env.ctnpygtfsy.us-east-2.elasticbeanstalk.com/user/image";
    public static final String MESSAGE_BODY_PREFIX = "The one time verification code for Citadel is ";

    public static String getAnotherUserImageUrl(String pk){
        String a = "http://bookshare-env.ctnpygtfsy.us-east-2.elasticbeanstalk.com/other_user/image?user_id=" + pk;
        return a;
    }
}
