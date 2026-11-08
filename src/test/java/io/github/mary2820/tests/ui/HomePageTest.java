package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.pages.HomePage;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.constants.IConstants.HOME_PAGE_TITLE;
import static io.github.mary2820.constants.IConstants.PAGE_NAME_HOME;

public class HomePageTest extends BaseUiTest {

    @Test
    @QaseId(151)
    public void openSiteTest() {
        HomePage homePage = new HomePage(page).open();

        assertThat(page, "%sTitle should be %s", PAGE_NAME_HOME, HOME_PAGE_TITLE).hasTitle(HOME_PAGE_TITLE);
        assertThat(homePage.logo(), "%sLogo should be visible", PAGE_NAME_HOME).isVisible();
    }

}
