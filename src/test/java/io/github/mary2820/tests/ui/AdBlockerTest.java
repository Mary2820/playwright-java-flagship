package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseTest;
import io.github.mary2820.config.Config;
import io.github.mary2820.pages.HomePage;
import io.github.mary2820.utils.AdBlocker;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class AdBlockerTest extends BaseTest {

    @Test
    public void homePageHasNoAdsTest() {
        if (!Config.blockAds())
            throw new SkipException("Ad blocking is disabled (-DblockAds=false)");

        new HomePage(page).open();

        // headless Chromium gets no ad iframes, so check what the ad and consent scripts inject instead
        assertThat(page.locator("ins.adsbygoogle")).hasCount(0);
        assertThat(page.locator(".fc-consent-root")).hasCount(0);
    }

    @Test
    public void isAdTest() {
        assertTrue(AdBlocker.isAd("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-1"));
        assertTrue(AdBlocker.isAd("https://fundingchoicesmessages.google.com/i/pub-1?ers=3"));
        assertFalse(AdBlocker.isAd("https://automationexercise.com/products"));
        assertFalse(AdBlocker.isAd("https://notdoubleclick.net/"));
        assertFalse(AdBlocker.isAd("https://fonts.googleapis.com/css?family=Archivo:400|Arimo:400"));
    }
}