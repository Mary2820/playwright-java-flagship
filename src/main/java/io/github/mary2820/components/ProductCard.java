package io.github.mary2820.components;

import com.microsoft.playwright.Locator;

// takes the card's Locator, not a Page, so the same logic works for any card on the page
public class ProductCard {
    private final Locator textName;
    private final Locator textPrice;
    private final Locator buttonAddToCart;

    public ProductCard(Locator card) {
        // a card has a second "Add to cart" in the hover overlay, so the locators are scoped to .productinfo
        Locator info = card.locator(".productinfo");
        this.textName = info.locator("p");
        this.textPrice = info.locator("h2");
        this.buttonAddToCart = info.getByText("Add to cart");
    }

    public Locator name() {
        return textName;
    }

    public Locator price() {
        return textPrice;
    }

    public void addToCart() {
        buttonAddToCart.click();
    }
}
