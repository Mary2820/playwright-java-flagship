package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.components.HeaderComponent;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.TestUser;
import io.github.mary2820.pages.LoginPage;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.constants.IConstants.*;

public class LoginTest extends BaseUiTest {
    private LoginPage loginPage;
    private HeaderComponent header;

    // runs after the BaseUiTest @BeforeMethod, so page already exists
    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        loginPage = new LoginPage(page);
        header = new HeaderComponent(page);
    }

    @Test(description = "Login with valid credentials", groups = PRIORITY_HIGH)
    @QaseId(76)
    public void userCanLoginWithValidCredentialsTest() {
        TestUser user = userService.create();

        loginPage.open().login(user.email(), user.password());

        assertThat(header.loggedInAs(), "%sHeader should show the user %s", PAGE_NAME_LOGIN, user.name())
                .hasText(LOGGED_IN_AS + user.name());
    }

    @Test(description = "Login with a wrong password", groups = PRIORITY_HIGH)
    @QaseId(77)
    public void loginFailsWithWrongPasswordTest() {
        TestUser user = userService.create();

        loginPage.open().login(user.email(), WRONG_PASSWORD);

        assertThat(loginPage.error(), "%sError should be %s", PAGE_NAME_LOGIN, LOGIN_ERROR).hasText(LOGIN_ERROR);
        assertThat(header.loggedInAs(), "%sUser should not be logged in", PAGE_NAME_LOGIN).isHidden();
    }

    @Test(description = "Signup with an already registered email", groups = PRIORITY_HIGH)
    @QaseId(90)
    public void signupFailsWhenEmailAlreadyRegisteredTest() {
        // the existing user comes from the API, a hardcoded one could be deleted by someone
        TestUser existing = userService.create();

        loginPage.open().signup("Another Name", existing.email());

        assertThat(loginPage.signupError(), "%sError should be %s", PAGE_NAME_LOGIN, SIGNUP_EMAIL_EXISTS_ERROR)
                .hasText(SIGNUP_EMAIL_EXISTS_ERROR);
    }

    @Test(description = "Login page opens with both forms", groups = PRIORITY_HIGH)
    @QaseId(70)
    public void loginPageShowsBothFormsTest() {
        loginPage.open();

        assertThat(loginPage.headingLogin(), "%sLogin heading should be shown", PAGE_NAME_LOGIN).isVisible();
        assertThat(loginPage.headingOr(), "%sOR heading should be shown", PAGE_NAME_LOGIN).isVisible();
        assertThat(loginPage.headingSignup(), "%sSignup heading should be shown", PAGE_NAME_LOGIN).isVisible();
        assertThat(loginPage.inputEmail(), "%sLogin email field should be shown", PAGE_NAME_LOGIN).isVisible();
        assertThat(loginPage.inputPassword(), "%sLogin password field should be shown", PAGE_NAME_LOGIN).isVisible();
        assertThat(loginPage.buttonLogin(), "%sLogin button should be shown", PAGE_NAME_LOGIN).isVisible();
        assertThat(loginPage.inputSignupName(), "%sSignup name field should be shown", PAGE_NAME_LOGIN).isVisible();
        assertThat(loginPage.inputSignupEmail(), "%sSignup email field should be shown", PAGE_NAME_LOGIN).isVisible();
        assertThat(loginPage.buttonSignup(), "%sSignup button should be shown", PAGE_NAME_LOGIN).isVisible();
    }

    @Test(description = "Login with an unregistered email", groups = PRIORITY_HIGH)
    @QaseId(78)
    public void loginFailsWithUnregisteredEmailTest() {
        loginPage.open().login(UNREGISTERED_EMAIL, WRONG_PASSWORD);

        assertThat(loginPage.error(), "%sError should be %s", PAGE_NAME_LOGIN, LOGIN_ERROR).hasText(LOGIN_ERROR);
        assertThat(header.loggedInAs(), "%sUser should not be logged in", PAGE_NAME_LOGIN).isHidden();
    }

    @Test(description = "Signup with a new email leads to account information", groups = PRIORITY_HIGH)
    @QaseId(89)
    public void signupWithNewEmailOpensAccountInformationTest() {
        // no account is created until the next form is submitted, so there is nothing to clean up
        TestUser newUser = TestUserFactory.random();

        loginPage.open().signup(SIGNUP_NAME, newUser.email());

        assertThat(loginPage.headingAccountInformation(), "%sAccount information form should open", PAGE_NAME_LOGIN)
                .hasText(ACCOUNT_INFO_HEADING);
    }
}
