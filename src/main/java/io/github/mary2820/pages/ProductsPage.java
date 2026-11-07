package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.github.mary2820.components.ProductCard;

public class ProductsPage extends BasePage {
    private static final String PATH = "/products";

    private final Locator inputSearch;
    private final Locator buttonSearch;
    private final Locator textTitle;
    private final Locator cards;
    private final Locator textNames;
    private final Locator linkViewCart;

    public ProductsPage(Page page) {
        super(page);
        this.inputSearch = page.getByPlaceholder("Search Product");
        this.buttonSearch = page.locator("#submit_search");
        this.textTitle = page.locator(".features_items .title");
        this.cards = page.locator(".features_items .product-image-wrapper");
        this.textNames = page.locator(".features_items .productinfo p");
        this.linkViewCart = page.locator("#cartModal")
                .getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("View Cart"));
}

    public ProductsPage open() {
        page.navigate(PATH);
        // a wait, not an assertion: if the page did not load, the failure names the search field
        inputSearch.waitFor();
        return this;
    }

    public void search(String term) {
        inputSearch.fill(term);
        buttonSearch.click();
    }

    public Locator title() {
        return textTitle;
    }

    public Locator productNames() {
        return textNames;
    }

    public ProductCard card(int index) {
        return new ProductCard(cards.nth(index));
    }

    // click() waits for the modal link to be visible, no explicit wait needed
    public void viewCartFromModal() {
        linkViewCart.click();
    }
}
