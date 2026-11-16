package io.github.mary2820.api;

import io.github.mary2820.models.TestUser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// the form fields of /api/createAccount and /api/updateAccount, in the names the site expects
public final class UserForm {
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final List<String> REQUIRED_FIELDS = List.of(NAME, EMAIL, PASSWORD, "firstname", "lastname",
            "address1", "country", "zipcode", "state", "city", "mobile_number");

    private UserForm() {
    }

    public static Map<String, String> fields(TestUser user) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put(NAME, user.name());
        form.put(EMAIL, user.email());
        form.put(PASSWORD, user.password());
        form.put("firstname", user.firstName());
        form.put("lastname", user.lastName());
        form.put("address1", user.address1());
        form.put("country", user.country());
        form.put("zipcode", user.zipcode());
        form.put("state", user.state());
        form.put("city", user.city());
        form.put("mobile_number", user.mobileNumber());
        return form;
    }

    public static Map<String, String> credentials(String email, String password) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put(EMAIL, email);
        form.put(PASSWORD, password);
        return form;
    }
}
