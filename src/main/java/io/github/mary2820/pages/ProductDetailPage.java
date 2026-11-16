package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class ProductDetailPage extends BasePage {
    private static final String PATH = "/product_details/";

    private final Locator blockInformation;
    private final Locator imageProduct;
    private final Locator textName;
    private final Locator textCategory;
    private final Locator textPrice;
    private final Locator textAvailability;
    private final Locator textCondition;
    private final Locator textBrand;
    private final Locator inputQuantity;
    private final Locator buttonAddToCart;
    private final Locator inputReviewName;
    private final Locator inputReviewEmail;
    private final Locator textareaReview;
    private final Locator buttonReviewSubmit;

    public ProductDetailPage(Page page) {
        super(page);
        this.blockInformation = page.locator(".product-information");
        this.imageProduct = page.locator(".view-product img");
        this.textName = blockInformation.locator("h2");
        // the price is the inner span, the outer one also holds the quantity field and the button
        this.textPrice = blockInformation.locator("span span");
        // the paragraphs have no ids, the label inside <b> or the "Category:" prefix identifies them
        this.textCategory = blockInformation.locator("p", new Locator.LocatorOptions().setHasText("Category:"));
        this.textAvailability = blockInformation.locator("p", new Locator.LocatorOptions().setHasText("Availability:"));
        this.textCondition = blockInformation.locator("p", new Locator.LocatorOptions().setHasText("Condition:"));
        this.textBrand = blockInformation.locator("p", new Locator.LocatorOptions().setHasText("Brand:"));
        this.inputQuantity = blockInformation.locator("#quantity");
        this.buttonAddToCart = blockInformation.getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Add to cart"));
        Locator formReview = page.locator("#review-form");
        this.inputReviewName = formReview.getByPlaceholder("Your Name");
        this.inputReviewEmail = formReview.getByPlaceholder("Email Address");
        this.textareaReview = formReview.getByPlaceholder("Add Review Here!");
        this.buttonReviewSubmit = formReview.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Submit"));
    }

    public ProductDetailPage open(int id) {
        navigate(PATH + id);
        // a wait, not an assertion: if the page did not load, the failure names the information block
        blockInformation.waitFor();
        return this;
    }

    public Locator image() {
        return imageProduct;
    }

    public Locator name() {
        return textName;
    }

    public Locator category() {
        return textCategory;
    }

    public Locator price() {
        return textPrice;
    }

    public Locator availability() {
        return textAvailability;
    }

    public Locator condition() {
        return textCondition;
    }

    public Locator brand() {
        return textBrand;
    }

    public Locator inputQuantity() {
        return inputQuantity;
    }

    public Locator buttonAddToCart() {
        return buttonAddToCart;
    }

    public Locator inputReviewName() {
        return inputReviewName;
    }

    public Locator inputReviewEmail() {
        return inputReviewEmail;
    }

    public Locator textareaReview() {
        return textareaReview;
    }

    public Locator buttonReviewSubmit() {
        return buttonReviewSubmit;
    }
}
