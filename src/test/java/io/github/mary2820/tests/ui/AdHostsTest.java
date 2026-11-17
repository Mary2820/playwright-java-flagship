package io.github.mary2820.tests.ui;

import io.github.mary2820.utils.AdBlocker;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertFalse;
import static io.github.mary2820.assertions.CustomAssert.assertTrue;
import static io.github.mary2820.constants.IConstants.*;

// a plain unit test of the host matching: it needs no browser, so it does not extend BaseUiTest
public class AdHostsTest {

    @Test(description = "Ad URLs are recognised", groups = PRIORITY_HIGH)
    public void isAdTest() {
        assertIsAd(AD_URL_ADSENSE);
        assertIsAd(AD_URL_CONSENT);
        assertIsNotAd(NOT_AD_URL_SITE);
        assertIsNotAd(NOT_AD_URL_LOOKALIKE);
        assertIsNotAd(NOT_AD_URL_FONTS);
    }

    private static void assertIsAd(String url) {
        assertTrue(AdBlocker.isAd(url), "%sURL %s should be blocked", PAGE_NAME_AD_BLOCKER, url);
    }

    private static void assertIsNotAd(String url) {
        assertFalse(AdBlocker.isAd(url), "%sURL %s should not be blocked", PAGE_NAME_AD_BLOCKER, url);
    }
}
