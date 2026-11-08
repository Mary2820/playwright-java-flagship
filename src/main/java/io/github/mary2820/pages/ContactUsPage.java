package io.github.mary2820.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.mary2820.constants.IConstants.CONTACT_US_FORM_HEADING;
import static io.github.mary2820.constants.IConstants.CONTACT_US_PATH;
import static io.github.mary2820.constants.IConstants.CONTACT_US_HEADING;

public class ContactUsPage extends BasePage {
    private final Locator headingContactUs;
    private final Locator headingGetInTouch;
    private final Locator formContact;
    private final Locator inputName;
    private final Locator inputEmail;
    private final Locator inputSubject;
    private final Locator inputMessage;
    private final Locator inputFile;
    private final Locator buttonSubmit;
    private final Locator textSuccess;
    // the page events arrive on another thread
    private final List<String> dialogMessages = new CopyOnWriteArrayList<>();
    private volatile boolean acceptDialogs;

    public ContactUsPage(Page page) {
        super(page);
        // a role locator reads the accessible name, so the <strong> inside "Contact <strong>Us</strong>" does not matter
        this.headingContactUs = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions()
                .setName(CONTACT_US_HEADING).setExact(true));
        this.headingGetInTouch = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions()
                .setName(CONTACT_US_FORM_HEADING).setExact(true));
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
        // Playwright dismisses dialogs by default and confirm() would return false; the handler is set before any
        // click, because a dialog blocks the page, and the page decides per submit whether to accept it
        page.onDialog(dialog -> {
            dialogMessages.add(dialog.message());
            if (acceptDialogs)
                dialog.accept();
            else
                dialog.dismiss();
        });
    }

    public ContactUsPage open() {
        page.navigate(CONTACT_US_PATH);
        // a wait, not an assertion: if the page did not load, the failure names the form
        formContact.waitFor();
        return this;
    }

    public void fill(String name, String email, String subject, String message, Path attachment) {
        fillName(name);
        fillEmail(email);
        fillSubject(subject);
        fillMessage(message);
        inputFile.setInputFiles(attachment);
    }

    public void fillName(String name) {
        inputName.fill(name);
    }

    public void fillEmail(String email) {
        inputEmail.fill(email);
    }

    public void fillSubject(String subject) {
        inputSubject.fill(subject);
    }

    public void fillMessage(String message) {
        inputMessage.fill(message);
    }

    // for a click that must not show a dialog; a dialog that does appear is dismissed and recorded
    public void submit() {
        acceptDialogs = false;
        buttonSubmit.click();
    }

    public void submitAndConfirm() {
        acceptDialogs = true;
        buttonSubmit.click();
    }

    public void submitAndCancel() {
        submit();
    }

    public List<String> dialogs() {
        return List.copyOf(dialogMessages);
    }

    public Locator success() {
        return textSuccess;
    }

    public Locator headingContactUs() {
        return headingContactUs;
    }

    public Locator headingGetInTouch() {
        return headingGetInTouch;
    }

    public Locator nameField() {
        return inputName;
    }

    public Locator emailField() {
        return inputEmail;
    }

    public Locator subjectField() {
        return inputSubject;
    }

    public Locator messageField() {
        return inputMessage;
    }

    public Locator fileField() {
        return inputFile;
    }

    public Locator submitButton() {
        return buttonSubmit;
    }

    // the browser's own validation: the message text depends on the browser language, the validity flags do not
    public boolean isEmailMissing() {
        return (Boolean) inputEmail.evaluate("e => e.validity.valueMissing");
    }

    public boolean isEmailInvalid() {
        return (Boolean) inputEmail.evaluate("e => e.validity.typeMismatch");
    }
}
