package io.github.mary2820.services;

import io.github.mary2820.api.UserApi;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.ApiResponse;
import io.github.mary2820.models.TestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static io.github.mary2820.constants.IConstants.CREATED;
import static io.github.mary2820.constants.IConstants.NOT_FOUND;
import static io.github.mary2820.constants.IConstants.OK;

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
        if (response.responseCode() != CREATED)
            throw new IllegalStateException("Setup failed: " + response);
        created.add(user);
        return user;
    }

    // for a user a test creates itself, when creating and deleting it is what the test checks
    public void track(TestUser user) {
        created.add(user);
    }

    // a failed cleanup is an environment issue, so it warns instead of failing the test
    public void cleanup() {
        for (TestUser user : created) {
            try {
                ApiResponse response = userApi.deleteAccount(user.email(), user.password());
                // the test may have deleted the user itself
                if (response.responseCode() == NOT_FOUND)
                    LOGGER.debug("Cleanup: {} is already deleted", user.email());
                else if (response.responseCode() != OK)
                    LOGGER.warn("Cleanup failed for {}: {}", user.email(), response);
            } catch (RuntimeException e) {
                LOGGER.warn("Cleanup failed for {}: {}", user.email(), e.getMessage());
            }
        }
        created.clear();
    }
}
