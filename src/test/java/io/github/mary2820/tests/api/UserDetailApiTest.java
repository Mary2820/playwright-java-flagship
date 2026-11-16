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
import static io.github.mary2820.assertions.CustomAssert.assertTrue;
import static io.github.mary2820.constants.IConstants.*;
import static io.restassured.RestAssured.given;

public class UserDetailApiTest extends BaseApiTest {

    @Test(description = "GET /getUserDetailByEmail returns the user", groups = PRIORITY_HIGH)
    @QaseId(64)
    public void userDetailReturnsUserTest() {
        TestUser registered = userService.create();

        UserResponse body = getUserDetail(registered.email());

        User user = body.user();
        assertEquals(body.responseCode(), OK, "%sresponseCode should be %s", PAGE_NAME_USER_API, OK);
        assertTrue(user != null, "%sUser should be returned", PAGE_NAME_USER_API);
        // the server issues the id, so only its presence can be checked
        assertTrue(user.id() > 0, "%sUser id should be set", PAGE_NAME_USER_API);
        assertEquals(user.name(), registered.name(), "%sName should be %s", PAGE_NAME_USER_API, registered.name());
        assertEquals(user.email(), registered.email(), "%sEmail should be %s", PAGE_NAME_USER_API, registered.email());
        assertEquals(user.firstName(), registered.firstName(), "%sFirst name should be %s", PAGE_NAME_USER_API,
                registered.firstName());
        assertEquals(user.lastName(), registered.lastName(), "%sLast name should be %s", PAGE_NAME_USER_API,
                registered.lastName());
        assertEquals(user.address1(), registered.address1(), "%sAddress should be %s", PAGE_NAME_USER_API,
                registered.address1());
        assertEquals(user.country(), registered.country(), "%sCountry should be %s", PAGE_NAME_USER_API,
                registered.country());
        assertEquals(user.zipcode(), registered.zipcode(), "%sZipcode should be %s", PAGE_NAME_USER_API,
                registered.zipcode());
        assertEquals(user.state(), registered.state(), "%sState should be %s", PAGE_NAME_USER_API,
                registered.state());
        assertEquals(user.city(), registered.city(), "%sCity should be %s", PAGE_NAME_USER_API, registered.city());
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
