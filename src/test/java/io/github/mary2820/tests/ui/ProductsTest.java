package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.pages.ProductDetailPage;
import io.github.mary2820.pages.ProductsPage;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.assertions.CustomAssert.assertTrue;
import static io.github.mary2820.constants.IConstants.*;

public class ProductsTest extends BaseUiTest {
    // the site also searches by category, "top" returns shirts without the word in the name, all "jeans" names have it
    private static final String SEARCH_TERM = "jeans";

    private ProductsPage products;
    private ProductDetailPage detail;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        products = new ProductsPage(page);
        detail = new ProductDetailPage(page);
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

    @Test(description = "Products page opens with search, sidebar and product cards", groups = PRIORITY_HIGH)
    @QaseId(105)
    public void productsPageShowsSearchSidebarAndCardsTest() {
        products.open();

        assertThat(products.title(), "%sTitle should be %s", PAGE_NAME_PRODUCTS, PRODUCTS_TITLE)
                .hasText(PRODUCTS_TITLE);
        assertThat(products.inputSearch(), "%sSearch field should be shown", PAGE_NAME_PRODUCTS).isVisible();
        assertThat(products.buttonSearch(), "%sSearch button should be shown", PAGE_NAME_PRODUCTS).isVisible();
        assertThat(products.headingCategory(), "%sCategory sidebar should be shown", PAGE_NAME_PRODUCTS).isVisible();
        assertThat(products.headingBrands(), "%sBrands sidebar should be shown", PAGE_NAME_PRODUCTS).isVisible();
        assertThat(products.cards(), "%sThere should be %s product cards", PAGE_NAME_PRODUCTS, PRODUCTS_COUNT)
                .hasCount(PRODUCTS_COUNT);
    }

    @Test(description = "View Product opens the matching product page", groups = PRIORITY_HIGH)
    @QaseId(109)
    public void viewProductOpensMatchingProductPageTest() {
        products.open();
        // read before the click: the card is gone after the navigation
        String expectedName = products.card(0).name().innerText();
        String expectedPrice = products.card(0).price().innerText();

        products.card(0).viewProduct();

        assertThat(page, "%sURL should be a product page", PAGE_NAME_PRODUCT_DETAIL)
                .hasPath(PRODUCT_DETAILS_PATH);
        assertThat(detail.name(), "%sName should be %s", PAGE_NAME_PRODUCT_DETAIL, expectedName)
                .hasText(expectedName);
        assertThat(detail.price(), "%sPrice should be %s", PAGE_NAME_PRODUCT_DETAIL, expectedPrice)
                .hasText(expectedPrice);
    }

    @Test(description = "Add to cart opens the confirmation modal", groups = PRIORITY_HIGH)
    @QaseId(118)
    public void addToCartOpensConfirmationModalTest() {
        products.open().card(0).addToCart();

        assertThat(products.modalTitle(), "%sModal title should be %s", PAGE_NAME_PRODUCTS, CART_ADDED_TITLE)
                .hasText(CART_ADDED_TITLE);
        assertThat(products.modalMessage(), "%sModal text should be %s", PAGE_NAME_PRODUCTS, CART_ADDED_MESSAGE)
                .hasText(CART_ADDED_MESSAGE);
        assertThat(products.buttonContinueShopping(), "%sContinue Shopping should be shown", PAGE_NAME_PRODUCTS)
                .isVisible();
        assertThat(products.linkViewCart(), "%sView Cart should be shown", PAGE_NAME_PRODUCTS).isVisible();
    }
}
