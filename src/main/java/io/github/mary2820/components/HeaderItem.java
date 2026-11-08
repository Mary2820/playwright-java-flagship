package io.github.mary2820.components;

import java.util.List;

// the items of the site header: the visible name and where the item leads
public enum HeaderItem {
    HOME("Home", "/"),
    PRODUCTS("Products", "/products"),
    CART("Cart", "/view_cart"),
    SIGNUP_LOGIN("Signup / Login", "/login"),
    TEST_CASES("Test Cases", "/test_cases"),
    API_TESTING("API Testing", "/api_list"),
    VIDEO_TUTORIALS("Video Tutorials", "https://www.youtube.com/c/AutomationExercise"),
    CONTACT_US("Contact us", "/contact_us"),
    LOGOUT("Logout", "/logout"),
    DELETE_ACCOUNT("Delete Account", "/delete_account");

    private final String label;
    private final String target;

    HeaderItem(String label, String target) {
        this.label = label;
        this.target = target;
    }

    public String label() {
        return label;
    }

    // a path on the site, or a full URL for an item that leaves it
    public String target() {
        return target;
    }

    // what a guest sees
    public static List<HeaderItem> guestItems() {
        return List.of(HOME, PRODUCTS, CART, SIGNUP_LOGIN, TEST_CASES, API_TESTING, VIDEO_TUTORIALS, CONTACT_US);
    }

    // what a logged in user sees besides the account items
    public static List<HeaderItem> commonItems() {
        return List.of(HOME, PRODUCTS, CART, TEST_CASES, API_TESTING, VIDEO_TUTORIALS, CONTACT_US);
    }

    // the items that lead to a page of the site
    public static List<HeaderItem> internalLinks() {
        return List.of(HOME, PRODUCTS, CART, SIGNUP_LOGIN, TEST_CASES, API_TESTING, CONTACT_US);
    }
}
