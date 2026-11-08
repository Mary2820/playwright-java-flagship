package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.pages.ProductsPage;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.assertions.CustomAssert.assertTrue;
import static io.github.mary2820.constants.IConstants.PAGE_NAME_PRODUCTS;
import static io.github.mary2820.constants.IConstants.PRIORITY_HIGH;
import static io.github.mary2820.constants.IConstants.SEARCHED_PRODUCTS_TITLE;

public class ProductsTest extends BaseUiTest {
    // the site also searches by category, "top" returns shirts without the word in the name, all "jeans" names have it
    private static final String SEARCH_TERM = "jeans";

    private ProductsPage products;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        products = new ProductsPage(page);
    }

    @Test(description = "Search returns only matching products", groups = PRIORITY_HIGH)
    @QaseId(111)
    public void searchReturnsOnlyMatchingProductsTest() {
        products.open().search(SEARCH_TERM);

        assertThat(products.title(), "%sTitle should be %s", PAGE_NAME_PRODUCTS, SEARCHED_PRODUCTS_TITLE)
                .hasText(SEARCHED_PRODUCTS_TITLE);
        // the page waits for the first product, so an empty result fails here and not as a passing empty loop
        for (String name : products.foundProductNames()) {
            assertTrue(name.toLowerCase().contains(SEARCH_TERM), "%sProduct name '%s' should contain %s",
                    PAGE_NAME_PRODUCTS, name, SEARCH_TERM);
        }
    }
}
