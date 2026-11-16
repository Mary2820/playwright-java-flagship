package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.github.mary2820.components.ProductCard;


import java.util.List;

public class ProductsPage extends BasePage {
    private static final String PATH = "/products";

    private final Locator inputSearch;
    private final Locator buttonSearch;
    private final Locator textTitle;
    private final Locator cards;
    private final Locator textNames;
    private final Locator headingCategory;
    private final Locator headingBrands;
    private final Locator textModalTitle;
    private final Locator textModalMessage;
    private final Locator buttonContinueShopping;
    private final Locator linkViewCart;

    public ProductsPage(Page page) {
        super(page);
        this.inputSearch = page.getByPlaceholder("Search Product");
        this.buttonSearch = page.locator("#submit_search");
        this.textTitle = page.locator(".features_items .title");
        this.cards = page.locator(".features_items .product-image-wrapper");
        this.textNames = page.locator(".features_items .productinfo p");
        this.headingCategory = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Category").setExact(true));
        this.headingBrands = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Brands").setExact(true));
        Locator modal = page.locator("#cartModal");
        this.textModalTitle = modal.locator(".modal-title");
        this.textModalMessage = modal.locator(".modal-body p").first();
        this.buttonContinueShopping = modal.getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Continue Shopping"));
        this.linkViewCart = modal.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("View Cart"));
    }

    public ProductsPage open() {
        navigate(PATH);
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

    // all() and allInnerTexts() do not wait, so the first name is awaited here: an empty result must fail
    // instead of giving an empty list that passes any check
    public List<String> foundProductNames() {
        textNames.first().waitFor();
        return textNames.allInnerTexts();
    }

    public ProductCard card(int index) {
        return new ProductCard(cards.nth(index));
    }

    // click() waits for the modal link to be visible, no explicit wait needed
    public void viewCartFromModal() {
        linkViewCart.click();
    }

    public Locator inputSearch() {
        return inputSearch;
    }

    public Locator buttonSearch() {
        return buttonSearch;
    }

    public Locator headingCategory() {
        return headingCategory;
    }

    public Locator headingBrands() {
        return headingBrands;
    }

    public Locator cards() {
        return cards;
    }

    public Locator modalTitle() {
        return textModalTitle;
    }

    public Locator modalMessage() {
        return textModalMessage;
    }

    public Locator buttonContinueShopping() {
        return buttonContinueShopping;
    }

    public Locator linkViewCart() {
        return linkViewCart;
    }
}
