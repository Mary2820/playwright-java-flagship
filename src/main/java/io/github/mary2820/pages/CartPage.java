package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CartPage extends BasePage {
    private static final String PATH = "/view_cart";

    private final Locator rows;

    public CartPage(Page page) {
        super(page);
        this.rows = page.locator("#cart_info_table tbody tr");
    }

    public CartPage open() {
        page.navigate(PATH);
        return this;
    }

    public Locator rows() {
        return rows;
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
