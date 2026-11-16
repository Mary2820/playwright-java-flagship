package io.github.mary2820.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

// the header is on every page, so pages share this component instead of copying its locators
public class HeaderComponent {
    private final Page page;
    private final Locator menu;
    private final Locator listItems;
    private final Locator textLoggedInAs;

    public HeaderComponent(Page page) {
        this.page = page;
        this.menu = page.locator(".shop-menu");
        this.listItems = menu.locator("li");
        this.textLoggedInAs = page.getByText("Logged in as");
    }

    // the name of a link holds an icon besides the text, so it is matched as a part, scoped to the menu
    public Locator item(HeaderItem item) {
        return menu.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(item.label()));
    }

    // every entry of the menu, including "Logged in as", which is not a link
    public Locator items() {
        return listItems;
    }

    public void open(HeaderItem item) {
        item(item).click();
    }

    public Locator loggedInAs() {
        return textLoggedInAs;
    }

    public void logout() {
        open(HeaderItem.LOGOUT);
    }
}
