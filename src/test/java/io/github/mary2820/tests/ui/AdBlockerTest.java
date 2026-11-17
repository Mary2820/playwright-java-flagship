package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.config.Config;
import io.github.mary2820.pages.HomePage;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.*;
import static io.github.mary2820.constants.IConstants.*;

public class AdBlockerTest extends BaseUiTest {

    @Test(description = "Home page has no ads", groups = PRIORITY_HIGH)
    public void homePageHasNoAdsTest() {
        if (!Config.blockAds())
            throw new SkipException("Ad blocking is disabled (-DblockAds=false)");

        HomePage homePage = new HomePage(page).open();

        // an empty page proves nothing, so first make sure the blocker really stopped some ad requests
        assertTrue(homePage.blockedAdRequests() > ZERO, "%sAt least one ad request should be blocked", PAGE_NAME_HOME);
        // headless Chromium gets no ad iframes, so check what the ad and consent scripts inject instead
        assertThat(homePage.adBlocks(), "%sAd blocks count should be %s", PAGE_NAME_HOME, ZERO).hasCount(ZERO);
        assertThat(homePage.consentPopup(), "%sConsent popup count should be %s", PAGE_NAME_HOME, ZERO)
                .hasCount(ZERO);
    }
}
