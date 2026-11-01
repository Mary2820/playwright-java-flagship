package io.github.mary2820.services;

import io.github.mary2820.api.UserApi;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.ApiResponse;
import io.github.mary2820.models.TestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

// keeps the users of one test, so an instance must not outlive that test
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserApi userApi;
    private final List<TestUser> created = new ArrayList<>();

    public UserService(UserApi userApi) {
        this.userApi = userApi;
    }

    // fails fast, so a broken setup is reported as such and not as a missing element later
    public TestUser create() {
        TestUser user = TestUserFactory.random();
        ApiResponse response = userApi.createAccount(user);
        if (response.responseCode() != 201)
            throw new IllegalStateException("Setup failed: " + response);
        created.add(user);
        return user;
    }

    // a failed cleanup is an environment issue, so it warns instead of failing the test
    public void cleanup() {
        for (TestUser user : created) {
            try {
                ApiResponse response = userApi.deleteAccount(user.email(), user.password());
                if (response.responseCode() != 200)
                    LOGGER.warn("Cleanup failed for {}: {}", user.email(), response);
            } catch (RuntimeException e) {
                LOGGER.warn("Cleanup failed for {}: {}", user.email(), e.getMessage());
            }
        }
        created.clear();
    }
}
