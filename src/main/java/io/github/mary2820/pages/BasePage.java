package io.github.mary2820.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;

public abstract class BasePage {
    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    // the load event waits for third-party resources, which the site sometimes serves past the timeout;
    // the page's open() waits for its own element instead, so the page is usable without it
    protected void navigate(String path) {
        page.navigate(path, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }
}
