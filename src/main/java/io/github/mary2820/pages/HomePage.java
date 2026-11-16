package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.github.mary2820.utils.AdBlocker;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class HomePage extends BasePage {
    private static final String PATH = "/";
    private static final String ABORTED_BY_ROUTE = "net::ERR_FAILED";

    private final Locator imgLogo;
    private final Locator blocksAd;
    private final Locator popupConsent;
    private final List<String> blockedAds = new CopyOnWriteArrayList<>();

    public HomePage(Page page) {
        super(page);
        this.imgLogo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions()
                .setName("Website for automation practice"));
        this.blocksAd = page.locator("ins.adsbygoogle");
        this.popupConsent = page.locator(".fc-consent-root");
        // route.abort() fails a request with net::ERR_FAILED; other network errors do not prove the blocker worked
        page.onRequestFailed(request -> {
            if (AdBlocker.isAd(request.url()) && ABORTED_BY_ROUTE.equals(request.failure()))
                blockedAds.add(request.url());
        });
    }

    public HomePage open() {
        page.navigate(PATH);
        return this;
    }

    public Locator logo() {
        return imgLogo;
    }

    public Locator adBlocks() {
        return blocksAd;
    }

    public Locator consentPopup() {
        return popupConsent;
    }

    public int blockedAdRequests() {
        return blockedAds.size();
    }
}
