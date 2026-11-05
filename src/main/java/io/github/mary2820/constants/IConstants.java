package io.github.mary2820.constants;

// interface fields are public static final by default; use them via static imports, never implement it
public interface IConstants {

    // prefixes for assertion messages, show where a check failed
    String PAGE_NAME_HOME = "[HOME screen]: ";
    String PAGE_NAME_LOGIN = "[LOGIN screen]: ";
    String PAGE_NAME_AD_BLOCKER = "[AD BLOCKER]: ";
    String PAGE_NAME_USER_API = "[USER API]: ";

    // pages
    String HOME_PAGE_TITLE = "Automation Exercise";

    // UI messages
    String LOGIN_ERROR = "Your email or password is incorrect!";
    String WRONG_PASSWORD = "wrong_password";

    // API endpoints
    String CREATE_ACCOUNT_ENDPOINT = "/api/createAccount";
    String DELETE_ACCOUNT_ENDPOINT = "/api/deleteAccount";
    String VERIFY_LOGIN_ENDPOINT = "/api/verifyLogin";

    // API response codes, sent in the JSON body, the HTTP status is always 200
    int OK = 200;
    int CREATED = 201;
    int BAD_REQUEST = 400;
    int NOT_FOUND = 404;

    // API response messages
    String USER_CREATED = "User created!";
    String USER_EXISTS = "User exists!";
    String ACCOUNT_DELETED = "Account deleted!";
    String USER_NOT_FOUND = "User not found!";
    String EMAIL_ALREADY_EXISTS = "Email already exists!";

    int ZERO = 0;
}