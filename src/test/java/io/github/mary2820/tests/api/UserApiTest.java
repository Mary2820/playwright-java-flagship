package io.github.mary2820.tests.api;

import io.github.mary2820.base.BaseApiTest;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.ApiResponse;
import io.github.mary2820.models.TestUser;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserApiTest extends BaseApiTest {

    @Test
    public void userLifecycleTest() {
        TestUser user = TestUserFactory.random();

        // not userService.create(): creating and deleting the user is what this test checks
        assertThat(userApi.createAccount(user)).isEqualTo(new ApiResponse(201, "User created!"));
        try {
            assertThat(userApi.verifyLogin(user.email(), user.password()))
                    .isEqualTo(new ApiResponse(200, "User exists!"));
            assertThat(userApi.deleteAccount(user.email(), user.password()))
                    .isEqualTo(new ApiResponse(200, "Account deleted!"));
            assertThat(userApi.verifyLogin(user.email(), user.password()))
                    .isEqualTo(new ApiResponse(404, "User not found!"));
        } finally {
            // removes the user if an assertion above failed, otherwise it answers 404
            userApi.deleteAccount(user.email(), user.password());
        }
    }

    @Test
    public void duplicateEmailIsRejectedTest() {
        TestUser user = userService.create();

        assertThat(userApi.createAccount(user))
                .isEqualTo(new ApiResponse(400, "Email already exists!"));
    }

    @Test
    public void wrongPasswordIsRejectedTest() {
        TestUser user = userService.create();

        assertThat(userApi.verifyLogin(user.email(), user.password() + "x"))
                .isEqualTo(new ApiResponse(404, "User not found!"));
    }
}
