package com.example.surveybackend.security;

import com.example.surveybackend.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_DATE = 1000*60*60*24*10; // 10 days
    public static final String LOGIN_URL = "/users/login";
    public static String getTokenSecret(){
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER_AUTHORIZATION = "Authorization";
}
