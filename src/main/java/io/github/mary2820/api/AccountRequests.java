package io.github.mary2820.api;

import io.github.mary2820.api.specs.ApiSpecs;
import io.github.mary2820.models.ApiResponse;
import io.github.mary2820.models.UserResponse;
import io.restassured.http.Method;

import java.util.Map;

import static io.github.mary2820.constants.IConstants.USER_DETAIL_ENDPOINT;
import static io.restassured.RestAssured.given;

// REST Assured calls of the account endpoints that answer HTTP 200 with the code in the body
public final class AccountRequests {

    private AccountRequests() {
    }

    public static ApiResponse send(Method method, String endpoint, Map<String, String> form) {
        return ApiSpecs.body(given().spec(ApiSpecs.request())
                .formParams(form)
                .request(method, endpoint)
                .then().spec(ApiSpecs.response()), ApiResponse.class);
    }

    public static UserResponse userDetail(String email) {
        return ApiSpecs.body(given().spec(ApiSpecs.request())
                .queryParam("email", email)
                .get(USER_DETAIL_ENDPOINT)
                .then().spec(ApiSpecs.response()), UserResponse.class);
    }
}
