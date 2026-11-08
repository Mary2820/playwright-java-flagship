package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.components.HeaderComponent;
import io.github.mary2820.components.HeaderItem;
import io.github.mary2820.models.TestUser;
import io.github.mary2820.pages.HomePage;
import io.github.mary2820.pages.LoginPage;
import io.github.mary2820.pages.ProductsPage;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.constants.IConstants.*;

public class HeaderTest extends BaseUiTest {
    private HomePage home;
    private HeaderComponent header;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        home = new HomePage(page);
        header = new HeaderComponent(page);
    }

    @DataProvider(name = "internalLinks")
    public Object[][] internalLinks() {
        return HeaderItem.internalLinks().stream().map(item -> new Object[]{item}).toArray(Object[][]::new);
    }

    private void loginThroughUi(TestUser user) {
        new LoginPage(page).open().login(user.email(), user.password());
        // the login must have worked before the header is checked, otherwise a guest header fails with a wrong reason
        assertThat(header.loggedInAs(), "%sSetup: user should be logged in", PAGE_NAME_HEADER)
                .hasText(LOGGED_IN_AS + user.name());
    }

    @Test(description = "Guest header shows all items", groups = PRIORITY_HIGH)
    @QaseId(24)
    public void guestHeaderShowsAllItemsTest() {
        home.open();

        assertThat(home.logo(), "%sLogo should be visible", PAGE_NAME_HEADER).isVisible();
        for (HeaderItem item : HeaderItem.guestItems()) {
            assertThat(header.item(item), "%sItem %s should be visible", PAGE_NAME_HEADER, item.label()).isVisible();
        }
        assertThat(header.items(), "%sHeader should have %s items", PAGE_NAME_HEADER, HeaderItem.guestItems().size())
                .hasCount(HeaderItem.guestItems().size());
    }

    @Test(description = "Internal links lead to their pages", dataProvider = "internalLinks", groups = PRIORITY_HIGH)
    @QaseId(25)
    public void internalLinksLeadToTheirPagesTest(HeaderItem item) {
        // start on another page than the target, so that the click has to move: Home from Home would prove nothing
        if (item == HeaderItem.PRODUCTS)
            home.open();
        else
            new ProductsPage(page).open();

        header.open(item);

        assertThat(page, "%sItem %s should lead to %s", PAGE_NAME_HEADER, item.label(), item.target())
                .hasPath(item.target());
    }

    @Test(description = "Header of an authenticated user", groups = PRIORITY_HIGH)
    @QaseId(30)
    public void authenticatedHeaderShowsUserAndAccountItemsTest() {
        TestUser user = userService.create();
        loginThroughUi(user);

        home.open();

        assertThat(header.loggedInAs(), "%sHeader should show the user %s", PAGE_NAME_HEADER, user.name())
                .hasText(LOGGED_IN_AS + user.name());
        assertThat(header.item(HeaderItem.LOGOUT), "%sLogout should be visible", PAGE_NAME_HEADER).isVisible();
        assertThat(header.item(HeaderItem.DELETE_ACCOUNT), "%sDelete Account should be visible", PAGE_NAME_HEADER)
                .isVisible();
        assertThat(header.item(HeaderItem.SIGNUP_LOGIN), "%sSignup / Login should not be shown", PAGE_NAME_HEADER)
                .isHidden();
        for (HeaderItem item : HeaderItem.commonItems()) {
            assertThat(header.item(item), "%sItem %s should be visible", PAGE_NAME_HEADER, item.label()).isVisible();
        }
    }

    @Test(description = "Logout from the header", groups = PRIORITY_HIGH)
    @QaseId(31)
    public void logoutReturnsToLoginAndShowsGuestHeaderTest() {
        TestUser user = userService.create();
        loginThroughUi(user);

        header.logout();

        assertThat(page, "%sLogout should lead to %s", PAGE_NAME_HEADER, HeaderItem.SIGNUP_LOGIN.target())
                .hasPath(HeaderItem.SIGNUP_LOGIN.target());
        assertThat(header.item(HeaderItem.SIGNUP_LOGIN), "%sSignup / Login should be shown", PAGE_NAME_HEADER)
                .isVisible();
        assertThat(header.loggedInAs(), "%sUser should not be logged in", PAGE_NAME_HEADER).isHidden();
    }
}
