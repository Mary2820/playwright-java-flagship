package io.github.mary2820.tests.ui;

import io.github.mary2820.utils.AdBlocker;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertFalse;
import static io.github.mary2820.assertions.CustomAssert.assertTrue;
import static io.github.mary2820.constants.IConstants.PAGE_NAME_AD_BLOCKER;

// a plain unit test of the host matching: it needs no browser, so it does not extend BaseUiTest
public class AdHostsTest {

    @Test(description = "Ad URLs are recognised")
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
