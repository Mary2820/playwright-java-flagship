package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.nio.file.Path;

public class ContactUsPage extends BasePage {
    private static final String PATH = "/contact_us";

    private final Locator formContact;
    private final Locator inputName;
    private final Locator inputEmail;
    private final Locator inputSubject;
    private final Locator inputMessage;
    private final Locator inputFile;
    private final Locator buttonSubmit;
    private final Locator textSuccess;

    public ContactUsPage(Page page) {
        super(page);
        this.formContact = page.locator("#contact-us-form");
        this.inputName = formContact.getByPlaceholder("Name");
        // "Email" is a substring of no other placeholder here, exact keeps it that way if the form grows
        this.inputEmail = formContact.getByPlaceholder("Email", new Locator.GetByPlaceholderOptions().setExact(true));
        this.inputSubject = formContact.getByPlaceholder("Subject");
        this.inputMessage = formContact.getByPlaceholder("Your Message Here");
        this.inputFile = formContact.locator("input[type=file]");
        this.buttonSubmit = formContact.locator("[data-qa=submit-button]");
        // the page has two .contact-form elements, the status block is inside the outer one
        this.textSuccess = page.locator(".contact-form .status");
    }

    public ContactUsPage open() {
        page.navigate(PATH);
        // a wait, not an assertion: if the page did not load, the failure names the form
        formContact.waitFor();
        return this;
    }

    public void fill(String name, String email, String subject, String message, Path attachment) {
        inputName.fill(name);
        inputEmail.fill(email);
        inputSubject.fill(subject);
        inputMessage.fill(message);
        inputFile.setInputFiles(attachment);
    }

    public void submit() {
        buttonSubmit.click();
    }

    public Locator success() {
        return textSuccess;
    }
}
