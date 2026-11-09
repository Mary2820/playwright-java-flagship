package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ProductDetailPage extends BasePage {
    private final Locator blockInformation;
    private final Locator textName;
    private final Locator textPrice;

    public ProductDetailPage(Page page) {
        super(page);
        this.blockInformation = page.locator(".product-information");
        this.textName = blockInformation.locator("h2");
        // the price is the inner span, the outer one also holds the quantity field and the button
        this.textPrice = blockInformation.locator("span span");
    }

    public Locator name() {
        return textName;
    }

    public Locator price() {
        return textPrice;
    }
}
