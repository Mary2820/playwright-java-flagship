package io.github.mary2820.tests.api;

import io.github.mary2820.api.specs.ApiSpecs;
import io.github.mary2820.base.BaseApiTest;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.TestUser;
import io.github.mary2820.models.User;
import io.github.mary2820.models.UserResponse;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertEquals;
import static io.github.mary2820.constants.IConstants.*;
import static io.restassured.RestAssured.given;

public class UserDetailApiTest extends BaseApiTest {

    @Test(description = "GET /getUserDetailByEmail returns the user", groups = PRIORITY_HIGH)
    @QaseId(64)
    public void userDetailReturnsUserTest() {
        TestUser registered = userService.create();

        UserResponse body = getUserDetail(registered.email());

        User expected = new User(body.user() == null ? 0 : body.user().id(), registered.name(), registered.email(),
                registered.firstName(), registered.lastName(), registered.address1(), registered.country(),
                registered.zipcode(), registered.state(), registered.city());
        assertEquals(body.responseCode(), OK, "%sresponseCode should be %s", PAGE_NAME_USER_API, OK);
        assertEquals(body.user(), expected, "%sUser should have the data given at registration", PAGE_NAME_USER_API);
    }

    @Test(description = "GET /getUserDetailByEmail without email is rejected", groups = PRIORITY_MEDIUM)
    @QaseId(65)
    public void userDetailWithoutEmailIsRejectedTest() {
        UserResponse body = ApiSpecs.body(given().spec(ApiSpecs.request())
                .get(USER_DETAIL_ENDPOINT)
                .then().spec(ApiSpecs.response()), UserResponse.class);

        assertEquals(body.responseCode(), BAD_REQUEST, "%sresponseCode should be %s", PAGE_NAME_USER_API, BAD_REQUEST);
        assertEquals(body.message(), EMAIL_PARAM_MISSING, "%sMessage should be %s", PAGE_NAME_USER_API,
                EMAIL_PARAM_MISSING);
    }

    @Test(description = "GET /getUserDetailByEmail for an unknown email", groups = PRIORITY_MEDIUM)
    @QaseId(66)
    public void userDetailForUnknownEmailIsNotFoundTest() {
        UserResponse body = getUserDetail(TestUserFactory.random().email());

        assertEquals(body.responseCode(), NOT_FOUND, "%sresponseCode should be %s", PAGE_NAME_USER_API, NOT_FOUND);
        assertEquals(body.message(), ACCOUNT_NOT_FOUND, "%sMessage should be %s", PAGE_NAME_USER_API,
                ACCOUNT_NOT_FOUND);
    }

    private UserResponse getUserDetail(String email) {
        return ApiSpecs.body(given().spec(ApiSpecs.request())
                .queryParam("email", email)
                .get(USER_DETAIL_ENDPOINT)
                .then().spec(ApiSpecs.response()), UserResponse.class);
    }
}
