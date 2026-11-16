package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.models.Product;
import io.github.mary2820.pages.ProductDetailPage;
import io.github.mary2820.pages.ProductsPage;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertEquals;
import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.constants.IConstants.*;

public class ProductDetailTest extends BaseUiTest {
    private ProductsPage products;
    private ProductDetailPage detail;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        products = new ProductsPage(page);
        detail = new ProductDetailPage(page);
    }

    @Test(description = "Product page opens with all product information", groups = PRIORITY_HIGH)
    @QaseId(125)
    public void productDetailPageShowsAllInformationTest() {
        detail.open(PRODUCT_ID);

        assertThat(detail.image(), "%sImage should be shown", PAGE_NAME_PRODUCT_DETAIL).isVisible();
        assertThat(detail.name(), "%sName should be %s", PAGE_NAME_PRODUCT_DETAIL, PRODUCT_NAME)
                .hasText(PRODUCT_NAME);
        assertThat(detail.category(), "%sCategory should be %s", PAGE_NAME_PRODUCT_DETAIL, PRODUCT_CATEGORY_TEXT)
                .hasText(PRODUCT_CATEGORY_TEXT);
        assertThat(detail.price(), "%sPrice should be %s", PAGE_NAME_PRODUCT_DETAIL, PRODUCT_PRICE)
                .hasText(PRODUCT_PRICE);
        assertThat(detail.availability(), "%sAvailability should be %s", PAGE_NAME_PRODUCT_DETAIL,
                PRODUCT_AVAILABILITY_TEXT).hasText(PRODUCT_AVAILABILITY_TEXT);
        assertThat(detail.condition(), "%sCondition should be %s", PAGE_NAME_PRODUCT_DETAIL, PRODUCT_CONDITION_TEXT)
                .hasText(PRODUCT_CONDITION_TEXT);
        assertThat(detail.brand(), "%sBrand should be %s", PAGE_NAME_PRODUCT_DETAIL, PRODUCT_BRAND_TEXT)
                .hasText(PRODUCT_BRAND_TEXT);
        assertThat(detail.inputQuantity(), "%sQuantity should be shown", PAGE_NAME_PRODUCT_DETAIL).isVisible();
        assertThat(detail.buttonAddToCart(), "%sAdd to cart should be shown", PAGE_NAME_PRODUCT_DETAIL).isVisible();
        assertThat(detail.inputReviewName(), "%sReview name field should be shown", PAGE_NAME_PRODUCT_DETAIL)
                .isVisible();
        assertThat(detail.inputReviewEmail(), "%sReview email field should be shown", PAGE_NAME_PRODUCT_DETAIL)
                .isVisible();
        assertThat(detail.textareaReview(), "%sReview text field should be shown", PAGE_NAME_PRODUCT_DETAIL)
                .isVisible();
        assertThat(detail.buttonReviewSubmit(), "%sReview submit button should be shown", PAGE_NAME_PRODUCT_DETAIL)
                .isVisible();
    }

    @Test(description = "Details match the catalog card and the API", groups = PRIORITY_HIGH)
    @QaseId(128)
    public void productDetailsMatchCardAndApiTest() {
        Product expected = productApi.list().get(0);
        products.open();
        // read before the click: the card is gone after the navigation
        String cardName = products.card(0).name().innerText();
        String cardPrice = products.card(0).price().innerText();
        // the first card must be the first API product, otherwise the test would compare two different products
        assertEquals(cardName, expected.name(), "%sCard 0 should be the first API product", PAGE_NAME_PRODUCT_API);
        assertEquals(cardPrice, expected.price(), "%sCard price should equal the API price", PAGE_NAME_PRODUCT_API);

        products.card(0).viewProduct();

        assertThat(detail.name(), "%sName should be %s", PAGE_NAME_PRODUCT_DETAIL, expected.name())
                .hasText(expected.name());
        assertThat(detail.price(), "%sPrice should be %s", PAGE_NAME_PRODUCT_DETAIL, expected.price())
                .hasText(expected.price());
        assertThat(detail.brand(), "%sBrand should be %s", PAGE_NAME_PRODUCT_DETAIL, expected.brand())
                .hasText("Brand: " + expected.brand());
        assertThat(detail.category(), "%sCategory should be %s", PAGE_NAME_PRODUCT_DETAIL, expected.categoryLabel())
                .hasText("Category: " + expected.categoryLabel());
    }
}
