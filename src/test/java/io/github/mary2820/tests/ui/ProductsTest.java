package io.github.mary2820.tests.ui;

import com.microsoft.playwright.Locator;
import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.pages.ProductsPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.constants.IConstants.PAGE_NAME_PRODUCTS;
import static io.github.mary2820.constants.IConstants.SEARCHED_PRODUCTS_TITLE;

public class ProductsTest extends BaseUiTest {
    // the site also searches by category, "top" returns shirts without the word in the name, all "jeans" names have it
    private static final String SEARCH_TERM = "jeans";

    private ProductsPage products;

    @BeforeMethod
    public void initPages() {
        products = new ProductsPage(page);
    }

    @Test
    public void searchReturnsOnlyMatchingProductsTest() {
        products.open().search(SEARCH_TERM);

        // all() does not wait, so these two checks come first: without them an empty result would pass the loop
        assertThat(products.title(), "%sTitle should be %s", PAGE_NAME_PRODUCTS, SEARCHED_PRODUCTS_TITLE)
                .hasText(SEARCHED_PRODUCTS_TITLE);
        assertThat(products.productNames().first(), "%sFound products should be visible", PAGE_NAME_PRODUCTS)
                .isVisible();

        for (Locator name : products.productNames().all()) {
            assertThat(name, "%sProduct name should contain %s", PAGE_NAME_PRODUCTS, SEARCH_TERM)
                    .containsText(Pattern.compile(SEARCH_TERM, Pattern.CASE_INSENSITIVE));
        }
    }
}
