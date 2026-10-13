package io.github.mary2820.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.github.mary2820.BaseTest;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static io.github.mary2820.Constants.BASE_URL;

public class HomePageTest extends BaseTest {

    @Test
    public void openSiteTest() {
        page.navigate(BASE_URL);

        assertThat(page).hasTitle("Automation Exercise");
        var logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Website for automation practice"));
        assertThat(logo).isVisible();
    }

}
