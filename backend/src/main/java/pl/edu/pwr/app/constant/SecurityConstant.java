/* This source code is licensed under MIT License (the "License").
   You may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   https://opensource.org/licenses/MIT

 */
package pl.edu.pwr.app.constant;

public class SecurityConstant {

    public static final long EXPIRATION_TIME = 432_000_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String GET_ARRAYS_LLC = "LLC";
    public static final String GET_ARRAYS_ADMINISTRATION = "Training App";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "Login first";
    public static final String ACCESS_DENIED_MESSAGE = "You don't have permission ";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {
            "/login", "/testpage", "/register","/send",
            "/resetpassword/**", "/users",
            "/find/**", "/logout", "/three_trainings",
            "/trainingImages/**", "/user/image/**", "/trainingImages/**"};

}
