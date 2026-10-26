package io.github.mary2820.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import io.github.mary2820.config.Config;
import io.github.mary2820.utils.AdBlocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
    private static final Path TRACES_DIR = Paths.get("traces");

    private Playwright pw;
    private Browser browser;
    private BrowserContext context;
    protected Page page;

    @BeforeClass
    public void setup(){
        pw = Playwright.create();
        LOGGER.info("Launching {} (headless={}, slowMo={}), baseUrl={}",
                Config.browser(), Config.headless(), Config.slowMo(), Config.baseUrl());
        browser = browserType().launch(new BrowserType.LaunchOptions()
                .setHeadless(Config.headless())
                .setSlowMo(Config.slowMo()));
    }

    private BrowserType browserType() {
        return switch (Config.browser().toLowerCase()) {
            case "chromium" -> pw.chromium();
            case "firefox" -> pw.firefox();
            case "webkit" -> pw.webkit();
            default -> throw new IllegalArgumentException("Unsupported browser: " + Config.browser());
        };
    }

    @BeforeMethod
    public void setupPage(Method method) {
        LOGGER.info("Test started: {}.{}", method.getDeclaringClass().getSimpleName(), method.getName());
        context = browser.newContext(getConfig());
        if (Config.blockAds()) {
            AdBlocker.apply(context);
        }
        context.setDefaultTimeout(Config.timeout());
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
    }

    private Browser.NewContextOptions getConfig() {
        return new Browser.NewContextOptions()
                .setBaseURL(Config.baseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupPage(ITestResult result) {
        if (context == null)
            return;
        String name = result.getTestClass().getRealClass().getSimpleName() + "." + result.getName();
        // keep trace.zip only for failed tests
        if (result.getStatus() == ITestResult.FAILURE) {
            Path trace = TRACES_DIR.resolve(name + ".zip");
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(trace));
            LOGGER.error("Test failed: {}, trace saved to {}", name, trace.toAbsolutePath());
        } else {
            context.tracing().stop();
            LOGGER.info("Test {}: {}", result.getStatus() == ITestResult.SUCCESS ? "passed" : "skipped", name);
        }
        context.close();
        context = null;
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() {
        if (pw != null)
            pw.close();
    }
}