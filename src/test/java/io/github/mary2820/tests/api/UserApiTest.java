package io.github.mary2820.tests.api;

import io.github.mary2820.base.BaseApiTest;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.ApiResponse;
import io.github.mary2820.models.TestUser;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertEquals;
import static io.github.mary2820.constants.IConstants.*;

public class UserApiTest extends BaseApiTest {

    @Test
    public void userLifecycleTest() {
        TestUser user = TestUserFactory.random();
        ApiResponse created = new ApiResponse(CREATED, USER_CREATED);
        ApiResponse loggedIn = new ApiResponse(OK, USER_EXISTS);
        ApiResponse deleted = new ApiResponse(OK, ACCOUNT_DELETED);
        ApiResponse notFound = new ApiResponse(NOT_FOUND, USER_NOT_FOUND);

        // not userService.create(): creating and deleting the user is what this test checks
        assertEquals(userApi.createAccount(user), created,
                "%sCreate account response should be %s", PAGE_NAME_USER_API, created);
        try {
            assertEquals(userApi.verifyLogin(user.email(), user.password()), loggedIn,
                    "%sLogin response for a new user should be %s", PAGE_NAME_USER_API, loggedIn);
            assertEquals(userApi.deleteAccount(user.email(), user.password()), deleted,
                    "%sDelete account response should be %s", PAGE_NAME_USER_API, deleted);
            assertEquals(userApi.verifyLogin(user.email(), user.password()), notFound,
                    "%sLogin response for a deleted user should be %s", PAGE_NAME_USER_API, notFound);
        } finally {
            // removes the user if an assertion above failed, otherwise it answers 404
            userApi.deleteAccount(user.email(), user.password());
        }
    }

    @Test
    public void duplicateEmailIsRejectedTest() {
        TestUser user = userService.create();
        ApiResponse rejected = new ApiResponse(BAD_REQUEST, EMAIL_ALREADY_EXISTS);

        assertEquals(userApi.createAccount(user), rejected,
                "%sCreate account response for a taken email should be %s", PAGE_NAME_USER_API, rejected);
    }

    @Test
    public void wrongPasswordIsRejectedTest() {
        TestUser user = userService.create();
        ApiResponse notFound = new ApiResponse(NOT_FOUND, USER_NOT_FOUND);

        assertEquals(userApi.verifyLogin(user.email(), user.password() + "x"), notFound,
                "%sLogin response for a wrong password should be %s", PAGE_NAME_USER_API, notFound);
    }
}