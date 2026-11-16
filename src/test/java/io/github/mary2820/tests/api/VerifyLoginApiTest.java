package io.github.mary2820.tests.api;

import io.github.mary2820.api.UserForm;
import io.github.mary2820.base.BaseApiTest;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.ApiResponse;
import io.github.mary2820.models.TestUser;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.Test;

import java.util.Map;

import static io.github.mary2820.api.AccountRequests.send;
import static io.github.mary2820.assertions.CustomAssert.assertEquals;
import static io.github.mary2820.constants.IConstants.*;
import static io.restassured.http.Method.DELETE;
import static io.restassured.http.Method.POST;

public class VerifyLoginApiTest extends BaseApiTest {

    @Test(description = "POST /verifyLogin without email is rejected", groups = PRIORITY_MEDIUM)
    @QaseId(47)
    public void verifyLoginWithoutEmailIsRejectedTest() {
        ApiResponse rejected = new ApiResponse(BAD_REQUEST, LOGIN_PARAMS_MISSING);

        assertEquals(send(POST, VERIFY_LOGIN_ENDPOINT, Map.of(UserForm.PASSWORD, TestUserFactory.random().password())),
                rejected, "%sLogin response without email should be %s", PAGE_NAME_USER_API, rejected);
    }

    @Test(description = "POST /verifyLogin without password is rejected", groups = PRIORITY_MEDIUM)
    @QaseId(48)
    public void verifyLoginWithoutPasswordIsRejectedTest() {
        ApiResponse rejected = new ApiResponse(BAD_REQUEST, LOGIN_PARAMS_MISSING);

        assertEquals(send(POST, VERIFY_LOGIN_ENDPOINT, Map.of(UserForm.EMAIL, TestUserFactory.random().email())),
                rejected, "%sLogin response without password should be %s", PAGE_NAME_USER_API, rejected);
    }

    @Test(description = "DELETE /verifyLogin is not supported", groups = PRIORITY_LOW)
    @QaseId(49)
    public void verifyLoginWithDeleteIsNotSupportedTest() {
        TestUser unknown = TestUserFactory.random();
        // unlike the account endpoints, this one answers HTTP 200 with the 405 in the body
        ApiResponse notSupported = new ApiResponse(METHOD_NOT_ALLOWED, METHOD_NOT_SUPPORTED);

        assertEquals(send(DELETE, VERIFY_LOGIN_ENDPOINT, UserForm.credentials(unknown.email(), unknown.password())),
                notSupported, "%sDELETE login response should be %s", PAGE_NAME_USER_API, notSupported);
    }

    @Test(description = "POST /verifyLogin with an unregistered email", groups = PRIORITY_HIGH)
    @QaseId(50)
    public void verifyLoginWithUnregisteredEmailIsNotFoundTest() {
        TestUser unknown = TestUserFactory.random();
        ApiResponse notFound = new ApiResponse(NOT_FOUND, USER_NOT_FOUND);

        assertEquals(send(POST, VERIFY_LOGIN_ENDPOINT, UserForm.credentials(unknown.email(), unknown.password())),
                notFound, "%sLogin response for an unregistered email should be %s", PAGE_NAME_USER_API, notFound);
    }
}
