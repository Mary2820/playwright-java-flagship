package io.github.mary2820.base;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import io.github.mary2820.api.UserApi;
import io.github.mary2820.config.Config;
import io.github.mary2820.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

// shared by UI and API tests, TestNG treats same-named lifecycle methods in subclasses as overrides
public abstract class BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    // package-private: the base classes need it, tests do not
    Playwright pw;
    private APIRequestContext apiRequest;
    protected UserApi userApi;
    protected UserService userService;

    @BeforeClass
    public void setupPlaywright() {
        pw = Playwright.create();
    }

    @BeforeMethod
    public void setupApi(Method method) {
        LOGGER.info("Test started: {}.{}", method.getDeclaringClass().getSimpleName(), method.getName());
        // separate from context.request(), so API calls do not share cookies with the browser
        apiRequest = pw.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(Config.baseUrl()));
        userApi = new UserApi(apiRequest);
        userService = new UserService(userApi);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupApi(ITestResult result) {
        if (userService != null) {
            userService.cleanup();
            userService = null;
        }
        if (apiRequest != null) {
            apiRequest.dispose();
            apiRequest = null;
        }
        logResult(result);
    }

    private void logResult(ITestResult result) {
        String name = testName(result);
        switch (result.getStatus()) {
            case ITestResult.SUCCESS -> LOGGER.info("Test passed: {}", name);
            case ITestResult.FAILURE -> LOGGER.error("Test failed: {}", name);
            default -> LOGGER.info("Test skipped: {}", name);
        }
    }

    static String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getName();
    }

    @AfterClass(alwaysRun = true)
    public void closePlaywright() {
        if (pw != null)
            pw.close();
    }
}
