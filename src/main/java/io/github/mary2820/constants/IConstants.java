package io.github.mary2820.constants;

// interface fields are public static final by default; use them via static imports, never implement it
public interface IConstants {

    // prefixes for assertion messages, show where a check failed
    String PAGE_NAME_HOME = "[HOME screen]: ";
    String PAGE_NAME_LOGIN = "[LOGIN screen]: ";
    String PAGE_NAME_HEADER = "[HEADER]: ";
    String PAGE_NAME_PRODUCTS = "[PRODUCTS screen]: ";
    String PAGE_NAME_CART = "[CART screen]: ";
    String PAGE_NAME_PRODUCT_DETAIL = "[PRODUCT DETAIL screen]: ";
    String PAGE_NAME_PRODUCT_API = "[PRODUCT API]: ";
    String PAGE_NAME_BRAND_API = "[BRAND API]: ";
    String PAGE_NAME_SEARCH_API = "[SEARCH API]: ";
    String PAGE_NAME_CONTACT_US = "[CONTACT US screen]: ";
    String PAGE_NAME_AD_BLOCKER = "[AD BLOCKER]: ";
    String PAGE_NAME_USER_API = "[USER API]: ";

    // TestNG groups, the same as the case priority in Qase; run one with -Dgroups=high
    String PRIORITY_HIGH = "high";
    String PRIORITY_MEDIUM = "medium";
    String PRIORITY_LOW = "low";

    // pages
    String HOME_PAGE_TITLE = "Automation Exercise";
    String CONTACT_US_PATH = "/contact_us";

    // UI messages
    String LOGGED_IN_AS = "Logged in as ";
    String LOGIN_ERROR = "Your email or password is incorrect!";
    String WRONG_PASSWORD = "wrong_password";
    String LOGIN_HEADING = "Login to your account";
    String OR_HEADING = "OR";
    String SIGNUP_HEADING = "New User Signup!";
    String ACCOUNT_INFO_HEADING = "Enter Account Information";
    // never registered: the site is shared, so the address is on a reserved domain nobody can own
    String UNREGISTERED_EMAIL = "no.such.user.autotest@example.test";
    String SIGNUP_NAME = "Test User";
    String PRODUCTS_TITLE = "All Products";
    // the size of the catalog on the live site, taken from the case; update it together with the case
    int PRODUCTS_COUNT = 34;
    String CART_ADDED_TITLE = "Added!";
    String CART_ADDED_MESSAGE = "Your product has been added to cart.";
    // a regex: hasPath puts it into a pattern, the id is not known in advance
    String PRODUCT_DETAILS_PATH = "/product_details/\\d+";
    // product 1 of the live catalog, the data of the product page case
    int PRODUCT_ID = 1;
    String PRODUCT_NAME = "Blue Top";
    String PRODUCT_CATEGORY_TEXT = "Category: Women > Tops";
    String PRODUCT_PRICE = "Rs. 500";
    String PRODUCT_AVAILABILITY_TEXT = "Availability: In Stock";
    String PRODUCT_CONDITION_TEXT = "Condition: New";
    String PRODUCT_BRAND_TEXT = "Brand: Polo";
    String SEARCHED_PRODUCTS_TITLE = "Searched Products";
    String DEFAULT_QUANTITY = "1";
    String EMPTY_CART_MESSAGE = "Cart is empty";
    // the site's own wording, "exist" without the s
    String SIGNUP_EMAIL_EXISTS_ERROR = "Email Address already exist!";
    String CONTACT_CONFIRM_DIALOG = "Press OK to proceed!";
    String CONTACT_SUCCESS = "submitted successfully";
    String CONTACT_NAME = "Test User";
    String CONTACT_SUBJECT = "Order question";
    String CONTACT_MESSAGE = "Hello from autotest";
    String CONTACT_US_HEADING = "Contact Us";
    String CONTACT_US_FORM_HEADING = "Get In Touch";

    // API endpoints
    String CREATE_ACCOUNT_ENDPOINT = "/api/createAccount";
    String DELETE_ACCOUNT_ENDPOINT = "/api/deleteAccount";
    String VERIFY_LOGIN_ENDPOINT = "/api/verifyLogin";
    String PRODUCTS_LIST_ENDPOINT = "/api/productsList";
    String BRANDS_LIST_ENDPOINT = "/api/brandsList";
    String USER_DETAIL_ENDPOINT = "/api/getUserDetailByEmail";
    String SEARCH_PRODUCT_ENDPOINT = "/api/searchProduct";
    String SEARCH_PARAM = "search_product";
    String SEARCH_TERM = "top";
    String SEARCH_NO_MATCH_TERM = "zzzzqqq";

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
    String SEARCH_PARAM_MISSING = "Bad request, search_product parameter is missing in POST request.";
    String ACCOUNT_NOT_FOUND = "Account not found with this email, try another email!";
    String EMAIL_PARAM_MISSING = "Bad request, email parameter is missing in GET request.";

    int ZERO = 0;
}