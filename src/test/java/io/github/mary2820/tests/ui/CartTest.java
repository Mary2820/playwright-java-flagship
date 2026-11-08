package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.components.ProductCard;
import io.github.mary2820.pages.CartPage;
import io.github.mary2820.pages.ProductsPage;
import org.testng.annotations.BeforeMethod;
import io.qase.commons.annotation.QaseIds;
import org.testng.annotations.Test;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.constants.IConstants.DEFAULT_QUANTITY;
import static io.github.mary2820.constants.IConstants.PAGE_NAME_CART;

public class CartTest extends BaseUiTest {
    private ProductsPage products;
    private CartPage cart;

    @BeforeMethod
    public void initPages() {
        products = new ProductsPage(page);
        cart = new CartPage(page);
    }

    @Test
    @QaseIds({120, 176})
    public void addedProductAppearsInCartWithCatalogPriceAndQuantityOneTest() {
        ProductCard card = products.open().card(0);
        // expected values come from the catalog, so a changed price or order does not break the test
        String expectedName = card.name().innerText();
        String expectedPrice = card.price().innerText();

        card.addToCart();
        products.viewCartFromModal();

        assertThat(cart.rows(), "%sCart should have 1 row", PAGE_NAME_CART).hasCount(1);
        assertThat(cart.nameIn(0), "%sProduct name should be %s", PAGE_NAME_CART, expectedName)
                .hasText(expectedName);
        assertThat(cart.priceIn(0), "%sProduct price should be %s", PAGE_NAME_CART, expectedPrice)
                .hasText(expectedPrice);
        assertThat(cart.quantityIn(0), "%sQuantity should be %s", PAGE_NAME_CART, DEFAULT_QUANTITY)
                .hasText(DEFAULT_QUANTITY);
    }
}
