package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.components.HeaderComponent;
import io.github.mary2820.models.TestUser;
import io.github.mary2820.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.constants.IConstants.*;

public class LoginTest extends BaseUiTest {
    private LoginPage loginPage;
    private HeaderComponent header;

    // runs after the BaseUiTest @BeforeMethod, so page already exists
    @BeforeMethod
    public void initPages() {
        loginPage = new LoginPage(page);
        header = new HeaderComponent(page);
    }

    @Test
    public void userCanLoginWithValidCredentialsTest() {
        TestUser user = userService.create();

        loginPage.open().login(user.email(), user.password());

        assertThat(header.loggedInAs(), "%sHeader should show the user %s", PAGE_NAME_LOGIN, user.name())
                .containsText(user.name());
    }

    @Test
    public void loginFailsWithWrongPasswordTest() {
        TestUser user = userService.create();

        loginPage.open().login(user.email(), WRONG_PASSWORD);

        assertThat(loginPage.error(), "%sError should be %s", PAGE_NAME_LOGIN, LOGIN_ERROR).hasText(LOGIN_ERROR);
        assertThat(header.loggedInAs(), "%sUser should not be logged in", PAGE_NAME_LOGIN).isHidden();
    }

    @Test
    public void signupFailsWhenEmailAlreadyRegisteredTest() {
        // the existing user comes from the API, a hardcoded one could be deleted by someone
        TestUser existing = userService.create();

        loginPage.open().signup("Another Name", existing.email());

        assertThat(loginPage.signupError(), "%sError should be %s", PAGE_NAME_LOGIN, SIGNUP_EMAIL_EXISTS_ERROR)
                .hasText(SIGNUP_EMAIL_EXISTS_ERROR);
    }
}
