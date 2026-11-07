package io.github.mary2820.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.github.mary2820.config.Config;
import io.github.mary2820.utils.AdBlocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BaseUiTest extends BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseUiTest.class);
    private static final Path TRACES_DIR = Paths.get("traces");

    private Browser browser;
    private BrowserContext context;
    protected Page page;

    @BeforeClass
    public void setupBrowser() {
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
    public void setupPage() {
        context = newContext(contextOptions());
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        page = context.newPage();
    }

    // template method: setupPage fixes the order of the steps, a test class overrides only the options
    protected Browser.NewContextOptions contextOptions() {
        return new Browser.NewContextOptions()
                .setBaseURL(Config.baseUrl());
    }

    // for contexts a test opens itself, so they get the same ad blocking and timeout as the default one;
    // the caller closes it
    protected BrowserContext newContext(Browser.NewContextOptions options) {
        BrowserContext created = browser.newContext(options);
        if (Config.blockAds()) {
            AdBlocker.apply(created);
        }
        created.setDefaultTimeout(Config.timeout());
        return created;
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupPage(ITestResult result) {
        if (context == null)
            return;
        try {
            stopTracing(result);
        } catch (RuntimeException e) {
            // a thrown error here would make TestNG skip the remaining tests of the class
            LOGGER.warn("Tracing stop failed for {}: {}", testName(result), e.getMessage());
        } finally {
            context.close();
            context = null;
        }
    }

    private void stopTracing(ITestResult result) {
        // keep trace.zip only for failed tests
        if (result.getStatus() == ITestResult.FAILURE) {
            Path trace = TRACES_DIR.resolve(testName(result) + ".zip");
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(trace));
            LOGGER.info("Trace saved to {}", trace.toAbsolutePath());
        } else {
            context.tracing().stop();
        }
    }
}
