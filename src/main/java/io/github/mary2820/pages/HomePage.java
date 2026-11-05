package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class HomePage extends BasePage {
    private static final String PATH = "/";

    private final Locator imgLogo;

    public HomePage(Page page) {
        super(page);
        this.imgLogo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions()
                .setName("Website for automation practice"));
    }

    public HomePage open() {
        page.navigate(PATH);
        return this;
    }

    public Locator logo() {
        return imgLogo;
    }
}
