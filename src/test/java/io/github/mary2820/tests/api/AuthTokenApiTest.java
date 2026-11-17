package io.github.mary2820.tests.api;

import io.github.mary2820.api.specs.ApiSpecs;
import io.github.mary2820.base.BaseApiTest;
import io.github.mary2820.models.AuthUser;
import io.github.mary2820.models.ErrorResponse;
import io.github.mary2820.models.LoginRequest;
import io.github.mary2820.models.LoginResponse;
import io.qase.commons.annotation.QaseId;
import io.restassured.response.ValidatableResponse;
import org.testng.annotations.Test;

import java.util.Map;

import static io.github.mary2820.assertions.CustomAssert.assertEquals;
import static io.github.mary2820.assertions.CustomAssert.assertFalse;
import static io.github.mary2820.constants.IConstants.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

// the token flow of dummyjson.com: automationexercise has no tokens, and unlike it this API sends real HTTP statuses
public class AuthTokenApiTest extends BaseApiTest {

    @Test(description = "POST /auth/login returns access and refresh tokens", groups = PRIORITY_HIGH)
    @QaseId(282)
    public void loginReturnsTokensTest() {
        ValidatableResponse response = login(new LoginRequest(DUMMY_USERNAME, DUMMY_PASSWORD), OK)
                .body(matchesJsonSchemaInClasspath("schemas/auth-login-response.json"));
        LoginResponse body = ApiSpecs.body(response, LoginResponse.class);

        assertFalse(body.accessToken().isBlank(), "%sAccess token should not be empty", PAGE_NAME_AUTH_API);
        assertFalse(body.refreshToken().isBlank(), "%sRefresh token should not be empty", PAGE_NAME_AUTH_API);
        assertEquals(body.username(), DUMMY_USERNAME, "%sUsername should be %s", PAGE_NAME_AUTH_API, DUMMY_USERNAME);
    }

    @Test(description = "GET /auth/me with a Bearer token returns the user", groups = PRIORITY_HIGH)
    @QaseId(283)
    public void meWithBearerTokenReturnsUserTest() {
        LoginResponse login = ApiSpecs.body(login(new LoginRequest(DUMMY_USERNAME, DUMMY_PASSWORD), OK),
                LoginResponse.class);

        AuthUser me = ApiSpecs.body(given().spec(ApiSpecs.dummyJsonRequest())
                .header(AUTHORIZATION, BEARER + login.accessToken())
                .get(AUTH_ME_ENDPOINT)
                .then().spec(ApiSpecs.jsonResponse(OK)), AuthUser.class);

        assertEquals(me.username(), DUMMY_USERNAME, "%sUsername should be %s", PAGE_NAME_AUTH_API, DUMMY_USERNAME);
        assertEquals(me.email(), login.email(), "%sEmail should equal the one from the login response: %s",
                PAGE_NAME_AUTH_API, login.email());
    }

    @Test(description = "GET /auth/me without a token is rejected", groups = PRIORITY_MEDIUM)
    @QaseId(284)
    public void meWithoutTokenIsRejectedTest() {
        ErrorResponse body = ApiSpecs.body(given().spec(ApiSpecs.dummyJsonRequest())
                .get(AUTH_ME_ENDPOINT)
                .then().spec(ApiSpecs.jsonResponse(UNAUTHORIZED)), ErrorResponse.class);

        assertEquals(body.message(), ACCESS_TOKEN_REQUIRED, "%sMessage should be %s", PAGE_NAME_AUTH_API,
                ACCESS_TOKEN_REQUIRED);
    }

    @Test(description = "POST /auth/login with a wrong password is rejected", groups = PRIORITY_MEDIUM)
    @QaseId(285)
    public void loginWithWrongPasswordIsRejectedTest() {
        ErrorResponse body = ApiSpecs.body(login(new LoginRequest(DUMMY_USERNAME, DUMMY_PASSWORD + WRONG_PASSWORD_SUFFIX),
                BAD_REQUEST), ErrorResponse.class);

        assertEquals(body.message(), INVALID_CREDENTIALS, "%sMessage should be %s", PAGE_NAME_AUTH_API,
                INVALID_CREDENTIALS);
    }

    @Test(description = "POST /auth/login without username and password is rejected", groups = PRIORITY_LOW)
    @QaseId(286)
    public void loginWithoutCredentialsIsRejectedTest() {
        ErrorResponse body = ApiSpecs.body(given().spec(ApiSpecs.dummyJsonRequest())
                .body(Map.of())
                .post(AUTH_LOGIN_ENDPOINT)
                .then().spec(ApiSpecs.jsonResponse(BAD_REQUEST)), ErrorResponse.class);

        assertEquals(body.message(), CREDENTIALS_REQUIRED, "%sMessage should be %s", PAGE_NAME_AUTH_API,
                CREDENTIALS_REQUIRED);
    }

    private ValidatableResponse login(LoginRequest request, int status) {
        return given().spec(ApiSpecs.dummyJsonRequest())
                .body(request)
                .post(AUTH_LOGIN_ENDPOINT)
                .then().spec(ApiSpecs.jsonResponse(status));
    }
}
