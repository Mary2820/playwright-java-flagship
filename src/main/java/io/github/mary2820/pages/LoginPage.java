package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static io.github.mary2820.constants.IConstants.*;

public class LoginPage extends BasePage {
    private static final String PATH = "/login";

    private final Locator headingLogin;
    private final Locator headingOr;
    private final Locator headingSignup;
    // opens after a successful signup step; it has no page object of its own yet
    private final Locator headingAccountInformation;
    private final Locator formLogin;
    private final Locator inputEmail;
    private final Locator inputPassword;
    private final Locator buttonLogin;
    private final Locator textError;
    private final Locator formSignup;
    private final Locator inputSignupName;
    private final Locator inputSignupEmail;
    private final Locator buttonSignup;
    private final Locator textSignupError;

    public LoginPage(Page page) {
        super(page);
        // the page has two forms with an "Email Address" field, so the locators are scoped to the login one
        this.headingLogin = heading(page, LOGIN_HEADING);
        this.headingOr = heading(page, OR_HEADING);
        this.headingSignup = heading(page, SIGNUP_HEADING);
        this.headingAccountInformation = heading(page, ACCOUNT_INFO_HEADING);
        this.formLogin = page.locator(".login-form");
        this.inputEmail = formLogin.getByPlaceholder("Email Address");
        this.inputPassword = formLogin.getByPlaceholder("Password");
        this.buttonLogin = formLogin.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Login"));
        this.textError = formLogin.locator("p");
        this.formSignup = page.locator(".signup-form");
        this.inputSignupName = formSignup.getByPlaceholder("Name");
        this.inputSignupEmail = formSignup.getByPlaceholder("Email Address");
        this.buttonSignup = formSignup.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Signup"));
        this.textSignupError = formSignup.locator("p");
    }

    private static Locator heading(Page page, String name) {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(name).setExact(true));
    }

    public LoginPage open() {
        navigate(PATH);
        // a wait, not an assertion: if the page did not load, the failure names the form and not the email field
        formLogin.waitFor();
        return this;
    }

    // void: the outcome is either a logged in user or an error, the test decides what to check
    public void login(String email, String password) {
        inputEmail.fill(email);
        inputPassword.fill(password);
        buttonLogin.click();
    }

    public void signup(String name, String email) {
        inputSignupName.fill(name);
        inputSignupEmail.fill(email);
        buttonSignup.click();
    }

    public Locator error() {
        return textError;
    }

    public Locator signupError() {
        return textSignupError;
    }

    public Locator headingLogin() {
        return headingLogin;
    }

    public Locator headingOr() {
        return headingOr;
    }

    public Locator headingSignup() {
        return headingSignup;
    }

    public Locator headingAccountInformation() {
        return headingAccountInformation;
    }

    public Locator inputEmail() {
        return inputEmail;
    }

    public Locator inputPassword() {
        return inputPassword;
    }

    public Locator buttonLogin() {
        return buttonLogin;
    }

    public Locator inputSignupName() {
        return inputSignupName;
    }

    public Locator inputSignupEmail() {
        return inputSignupEmail;
    }

    public Locator buttonSignup() {
        return buttonSignup;
    }
}
