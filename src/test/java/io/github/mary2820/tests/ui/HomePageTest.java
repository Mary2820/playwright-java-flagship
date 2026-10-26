package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseTest;
import io.github.mary2820.pages.HomePage;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HomePageTest extends BaseTest {

    @Test
    public void openSiteTest() {
        HomePage homePage = new HomePage(page).open();

        assertThat(page).hasTitle("Automation Exercise");
        assertThat(homePage.logo()).isVisible();
    }

}