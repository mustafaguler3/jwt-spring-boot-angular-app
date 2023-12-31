package com.example.demo.constants;

public class SecurityConstant {

    public static final long EXPIRATION_TIME = 432_000_000; // 5 days
    public static final String TOKEN_HEADER = "Bearer ";
    public static final String ACCESS_DENIED_MESSAGE = "Access denied";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String FORBIDDEN_MESSAGE = " ";
    public static final String GET_ARRAYS = "Get arrays";
    public static final String ADMINISTRATION = "User management portal";
    public static final String AUTHORITIES = "authorities";


    public static final String[] PUBLIC_URLS = {"/user/login","/user/register","/user/resetPassword","/user/image/**"};
}




















