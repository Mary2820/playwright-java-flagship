package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CartPage extends BasePage {
    private static final String PATH = "/view_cart";

    private final Locator rows;
    private final Locator textEmptyCart;

    public CartPage(Page page) {
        super(page);
        this.rows = page.locator("#cart_info_table tbody tr");
        this.textEmptyCart = page.locator("#empty_cart");
    }

    public CartPage open() {
        page.navigate(PATH);
        return this;
    }

    public Locator rows() {
        return rows;
    }

    public Locator emptyCart() {
        return textEmptyCart;
    }

    // the row is removed by JS without a reload, so the test waits for it with a web-first assertion
    public void deleteIn(int index) {
        // cart.js is loaded at the end of the page and binds the click handler: the row is in the DOM before that,
        // and an early click does nothing, so wait for the load event
        page.waitForLoadState();
        rows.nth(index).locator(".cart_quantity_delete").click();
    }

    public Locator nameIn(int index) {
        return rows.nth(index).locator(".cart_description h4 a");
    }

    public Locator priceIn(int index) {
        return rows.nth(index).locator(".cart_price p");
    }

    public Locator quantityIn(int index) {
        return rows.nth(index).locator(".cart_quantity button");
    }
}
