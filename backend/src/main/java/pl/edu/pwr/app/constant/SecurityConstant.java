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
    public static final String[] PUBLIC_URLS = { "/login","/proposals", "/testpage", "/register", "/resetpassword/**", "/trainings", "/users", "/find/**" };

}
