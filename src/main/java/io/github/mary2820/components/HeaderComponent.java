package io.github.mary2820.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

// the header is on every page, so pages share this component instead of copying its locators
public class HeaderComponent {
    private final Locator textLoggedInAs;
    private final Locator linkLogout;

    public HeaderComponent(Page page) {
        this.textLoggedInAs = page.getByText("Logged in as");
        this.linkLogout = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Logout"));
    }

    public Locator loggedInAs() {
        return textLoggedInAs;
    }

    public void logout() {
        linkLogout.click();
    }
}
