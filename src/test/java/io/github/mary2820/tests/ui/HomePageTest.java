package io.github.mary2820.tests.ui;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.github.mary2820.base.BaseTest;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class HomePageTest extends BaseTest {

    @Test
    public void openSiteTest() {
        page.navigate("/");

        assertThat(page).hasTitle("Automation Exercise");
        var logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Website for automation practice"));
        assertThat(logo).isVisible();
    }

}
