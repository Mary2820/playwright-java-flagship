package io.github.mary2820.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import io.github.mary2820.models.ApiResponse;
import io.github.mary2820.models.TestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.mary2820.constants.IConstants.*;

public class UserApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserApi.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final APIRequestContext request;

    public UserApi(APIRequestContext request) {
        this.request = request;
    }

    public ApiResponse createAccount(TestUser user) {
        FormData form = FormData.create()
                .set("name", user.name())
                .set("email", user.email())
                .set("password", user.password())
                .set("firstname", user.firstName())
                .set("lastname", user.lastName())
                .set("address1", user.address1())
                .set("country", user.country())
                .set("zipcode", user.zipcode())
                .set("state", user.state())
                .set("city", user.city())
                .set("mobile_number", user.mobileNumber());
        ApiResponse response = parse(request.post(CREATE_ACCOUNT_ENDPOINT, RequestOptions.create().setForm(form)));
        LOGGER.info("createAccount {} -> {}", user.email(), response);
        return response;
    }

    public ApiResponse deleteAccount(String email, String password) {
        FormData form = FormData.create()
                .set("email", email)
                .set("password", password);
        ApiResponse response = parse(request.delete(DELETE_ACCOUNT_ENDPOINT, RequestOptions.create().setForm(form)));
        LOGGER.info("deleteAccount {} -> {}", email, response);
        return response;
    }

    public ApiResponse verifyLogin(String email, String password) {
        FormData form = FormData.create()
                .set("email", email)
                .set("password", password);
        return parse(request.post(VERIFY_LOGIN_ENDPOINT, RequestOptions.create().setForm(form)));
    }

    private ApiResponse parse(APIResponse response) {
        try {
            return MAPPER.readValue(response.text(), ApiResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Not a JSON response: " + response.status() + " " + response.text(), e);
        }
    }
}
