package io.github.mary2820;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import static io.github.mary2820.Constants.THOUSAND;

public class BaseTest {
    private Playwright pw;
    private Browser browser;
    private BrowserContext context;
    protected Page page;

    @BeforeClass
    public void setup(){
        pw = Playwright.create();
        browser = pw.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(THOUSAND));
    }

    @BeforeMethod
    public void setupPage() {
        context = browser.newContext(getConfig());
        page = context.newPage();
    }

    private Browser.NewContextOptions getConfig() {
        // TODO: read baseURL / timeouts from config file
        return new Browser.NewContextOptions();
    }

    @AfterMethod
    public void cleanupPage() {
        if (context != null)
            context.close();
    }

    @AfterClass
    public void cleanup() {
        if (pw != null)
            pw.close();
    }
}
