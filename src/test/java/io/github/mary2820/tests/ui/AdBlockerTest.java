package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.config.Config;
import io.github.mary2820.pages.HomePage;
import io.github.mary2820.utils.AdBlocker;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.*;
import static io.github.mary2820.constants.IConstants.*;

public class AdBlockerTest extends BaseUiTest {

    @Test
    public void homePageHasNoAdsTest() {
        if (!Config.blockAds())
            throw new SkipException("Ad blocking is disabled (-DblockAds=false)");

        new HomePage(page).open();

        // headless Chromium gets no ad iframes, so check what the ad and consent scripts inject instead
        assertThat(page.locator("ins.adsbygoogle"), "%sAd blocks count should be %s", PAGE_NAME_HOME, ZERO).hasCount(ZERO);
        assertThat(page.locator(".fc-consent-root"), "%sConsent popup count should be %s", PAGE_NAME_HOME, ZERO).hasCount(ZERO);
    }

    @Test
    public void isAdTest() {
        assertIsAd("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-1");
        assertIsAd("https://fundingchoicesmessages.google.com/i/pub-1?ers=3");
        assertIsNotAd("https://automationexercise.com/products");
        assertIsNotAd("https://notdoubleclick.net/");
        assertIsNotAd("https://fonts.googleapis.com/css?family=Archivo:400|Arimo:400");
    }

    private static void assertIsAd(String url) {
        assertTrue(AdBlocker.isAd(url), "%sURL %s should be blocked", PAGE_NAME_AD_BLOCKER, url);
    }

    private static void assertIsNotAd(String url) {
        assertFalse(AdBlocker.isAd(url), "%sURL %s should not be blocked", PAGE_NAME_AD_BLOCKER, url);
    }
}