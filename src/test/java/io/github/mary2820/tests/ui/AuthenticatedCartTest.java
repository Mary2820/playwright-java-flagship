package io.github.mary2820.tests.ui;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.github.mary2820.api.UserApi;
import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.components.HeaderComponent;
import io.github.mary2820.models.TestUser;
import io.github.mary2820.pages.CartPage;
import io.github.mary2820.pages.LoginPage;
import io.github.mary2820.pages.ProductsPage;
import io.github.mary2820.services.UserService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.constants.IConstants.PAGE_NAME_CART;
import static io.github.mary2820.constants.IConstants.PAGE_NAME_LOGIN;

public class AuthenticatedCartTest extends BaseUiTest {
    // the file holds a live session, so it stays in target/, which is gitignored; never commit it
    private static final Path STATE = Paths.get("target/auth/authenticated-cart.json");

    // the per-test userApi and userService do not exist yet in @BeforeClass, so the class has its own
    private APIRequestContext classApiRequest;
    private UserService classUserService;
    private TestUser user;

    // runs after BaseUiTest.setupBrowser, TestNG calls the parent's @BeforeClass first
    @BeforeClass
    public void loginOnceAndSaveState() throws IOException {
        classApiRequest = newApiRequest();
        classUserService = new UserService(new UserApi(classApiRequest));
        user = classUserService.create();

        Files.createDirectories(STATE.getParent());
        // the site API only checks the credentials and gives no session, so the login goes through the UI, once
        try (BrowserContext loginContext = newContext(super.contextOptions())) {
            Page loginPage = loginContext.newPage();
            new LoginPage(loginPage).open().login(user.email(), user.password());
            // fail fast: a silent login failure would save a guest state and break every test with an unclear error
            assertThat(new HeaderComponent(loginPage).loggedInAs(), "%sSetup: user should be logged in", PAGE_NAME_LOGIN)
                    .containsText(user.name());
            loginContext.storageState(new BrowserContext.StorageStateOptions().setPath(STATE));
        }
    }

    @Override
    protected Browser.NewContextOptions contextOptions() {
        return super.contextOptions().setStorageStatePath(STATE);
    }

    @Test
    public void cartPersistsAcrossSessionsForLoggedInUserTest() {
        // no login step here: the header already shows the user, which proves the state works
        ProductsPage products = new ProductsPage(page);
        products.open();
        String expectedName = products.card(0).name().innerText();
        products.card(0).addToCart();
        products.viewCartFromModal();
        assertThat(new CartPage(page).rows(), "%sCart should have 1 row in the first session", PAGE_NAME_CART)
                .hasCount(1);

        // a second context is a second device of the same user
        try (BrowserContext secondSession = newContext(contextOptions())) {
            Page secondPage = secondSession.newPage();
            CartPage cartInSecondSession = new CartPage(secondPage).open();

            assertThat(new HeaderComponent(secondPage).loggedInAs(), "%sSecond session should be logged in", PAGE_NAME_LOGIN)
                    .containsText(user.name());
            assertThat(cartInSecondSession.rows(), "%sCart should have 1 row in the second session", PAGE_NAME_CART)
                    .hasCount(1);
            assertThat(cartInSecondSession.nameIn(0), "%sProduct should be %s", PAGE_NAME_CART, expectedName)
                    .hasText(expectedName);
        }
    }

    @AfterClass(alwaysRun = true)
    public void deleteUser() {
        if (classUserService != null)
            classUserService.cleanup();
        if (classApiRequest != null)
            classApiRequest.dispose();
    }
}
