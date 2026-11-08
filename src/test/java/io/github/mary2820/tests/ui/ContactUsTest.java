package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.TestUser;
import io.github.mary2820.pages.ContactUsPage;
import io.qase.commons.annotation.QaseId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static io.github.mary2820.assertions.CustomAssert.*;
import static io.github.mary2820.constants.IConstants.*;

public class ContactUsTest extends BaseUiTest {
    // a relative path inside the project, an absolute one would break on CI
    private static final Path ATTACHMENT = Paths.get("src/test/resources/files/contact.txt");

    private ContactUsPage contactUs;

    @BeforeMethod(alwaysRun = true)
    public void initPages() {
        contactUs = new ContactUsPage(page);
    }

    // dialogs are dismissed unless accepted, so the list shows which ones appeared; the handler is set before the click,
    // because a dialog blocks the page
    private List<String> recordDialogs(boolean accept) {
        List<String> messages = new ArrayList<>();
        page.onDialog(dialog -> {
            messages.add(dialog.message());
            if (accept)
                dialog.accept();
            else
                dialog.dismiss();
        });
        return messages;
    }

    @DataProvider(name = "invalidEmails")
    public Object[][] invalidEmails() {
        return new Object[][]{{"abc"}, {"abc@"}, {"@mail.com"}, {"a b@mail.com"}};
    }

    @Test(description = "Form is submitted after confirming the dialog", groups = PRIORITY_HIGH)
    @QaseId(2)
    public void contactFormIsSubmittedAfterConfirmingDialogTest() {
        // the form needs only a name and an email, so no account is created through the API
        TestUser user = TestUserFactory.random();
        // a lambda cannot reassign a local variable, so the message goes into a list
        List<String> dialogMessages = new ArrayList<>();
        // Playwright dismisses dialogs by default, confirm() would return false and the form would not be sent;
        // the handler is set before the click, because the dialog blocks the page, and once so a stray dialog is not accepted
        page.onceDialog(dialog -> {
            dialogMessages.add(dialog.message());
            dialog.accept();
        });

        contactUs.open().fill(user.name(), user.email(), "Order question", "Hello from autotest", ATTACHMENT);
        contactUs.submit();

        assertThat(contactUs.success(), "%sForm should be submitted", PAGE_NAME_CONTACT_US)
                .containsText(CONTACT_SUCCESS);
        assertEquals(dialogMessages, List.of(CONTACT_CONFIRM_DIALOG), "%sOnly the confirm dialog should appear",
                PAGE_NAME_CONTACT_US);
    }

    @Test(description = "Page opens and shows all form elements", groups = PRIORITY_HIGH)
    @QaseId(1)
    public void contactPageShowsAllFormElementsTest() {
        contactUs.open();

        assertThat(contactUs.headingContactUs(), "%sHeading should be %s", PAGE_NAME_CONTACT_US, CONTACT_US_HEADING)
                .isVisible();
        assertThat(contactUs.headingGetInTouch(), "%sHeading should be %s", PAGE_NAME_CONTACT_US,
                CONTACT_US_FORM_HEADING).isVisible();
        assertThat(contactUs.nameField(), "%sName field should be visible", PAGE_NAME_CONTACT_US).isVisible();
        assertThat(contactUs.emailField(), "%sEmail field should be visible", PAGE_NAME_CONTACT_US).isVisible();
        assertThat(contactUs.subjectField(), "%sSubject field should be visible", PAGE_NAME_CONTACT_US).isVisible();
        assertThat(contactUs.messageField(), "%sMessage field should be visible", PAGE_NAME_CONTACT_US).isVisible();
        assertThat(contactUs.fileField(), "%sFile chooser should be visible", PAGE_NAME_CONTACT_US).isVisible();
        assertThat(contactUs.submitButton(), "%sSubmit button should be visible", PAGE_NAME_CONTACT_US).isVisible();
    }

    @Test(description = "Cancelling the dialog does not send the form", groups = PRIORITY_HIGH)
    @QaseId(4)
    public void cancelledDialogDoesNotSendFormTest() {
        TestUser user = TestUserFactory.random();
        List<String> dialogMessages = recordDialogs(false);

        contactUs.open().fill(user.name(), user.email(), "Order question", "Hello from autotest", ATTACHMENT);
        contactUs.submit();

        assertEquals(dialogMessages, List.of(CONTACT_CONFIRM_DIALOG), "%sOnly the confirm dialog should appear",
                PAGE_NAME_CONTACT_US);
        assertThat(contactUs.success(), "%sSuccess message should not be shown", PAGE_NAME_CONTACT_US).isHidden();
        assertThat(page, "%sForm should stay on the page", PAGE_NAME_CONTACT_US)
                .hasUrl(Pattern.compile(".*" + CONTACT_US_PATH + "$"));
        assertThat(contactUs.nameField(), "%sName should be kept", PAGE_NAME_CONTACT_US).hasValue(user.name());
        assertThat(contactUs.emailField(), "%sEmail should be kept", PAGE_NAME_CONTACT_US).hasValue(user.email());
        assertThat(contactUs.subjectField(), "%sSubject should be kept", PAGE_NAME_CONTACT_US)
                .hasValue("Order question");
        assertThat(contactUs.messageField(), "%sMessage should be kept", PAGE_NAME_CONTACT_US)
                .hasValue("Hello from autotest");
    }

    @Test(description = "Form is submitted with the required Email only", groups = PRIORITY_HIGH)
    @QaseId(7)
    public void emailOnlyFormIsSubmittedTest() {
        TestUser user = TestUserFactory.random();
        List<String> dialogMessages = recordDialogs(true);

        contactUs.open().fillEmail(user.email());
        // after the submit the fields are gone from the page, so the empty ones are checked before it
        assertThat(contactUs.nameField(), "%sName should be empty", PAGE_NAME_CONTACT_US).hasValue("");
        assertThat(contactUs.subjectField(), "%sSubject should be empty", PAGE_NAME_CONTACT_US).hasValue("");
        assertThat(contactUs.messageField(), "%sMessage should be empty", PAGE_NAME_CONTACT_US).hasValue("");
        contactUs.submit();

        assertEquals(dialogMessages, List.of(CONTACT_CONFIRM_DIALOG), "%sOnly the confirm dialog should appear",
                PAGE_NAME_CONTACT_US);
        assertThat(contactUs.success(), "%sForm should be submitted", PAGE_NAME_CONTACT_US)
                .containsText(CONTACT_SUCCESS);
    }

    @Test(description = "Empty Email blocks the submission", groups = PRIORITY_HIGH)
    @QaseId(8)
    public void emptyEmailBlocksSubmissionTest() {
        List<String> dialogMessages = recordDialogs(false);

        contactUs.open();
        contactUs.fillName("Test User");
        contactUs.fillSubject("Order question");
        contactUs.fillMessage("Hello from autotest");
        contactUs.submit();

        assertTrue(contactUs.isEmailMissing(), "%sEmail should be reported as required", PAGE_NAME_CONTACT_US);
        // the browser moves the focus to the field it complains about
        assertThat(contactUs.emailField(), "%sEmail should be focused", PAGE_NAME_CONTACT_US).isFocused();
        assertEmpty(dialogMessages, "%sNo dialog should appear", PAGE_NAME_CONTACT_US);
    }

    @Test(description = "Invalid Email formats are rejected", dataProvider = "invalidEmails", groups = PRIORITY_HIGH)
    @QaseId(9)
    public void invalidEmailFormatsAreRejectedTest(String email) {
        List<String> dialogMessages = recordDialogs(false);

        contactUs.open().fillEmail(email);
        contactUs.submit();

        assertTrue(contactUs.isEmailInvalid(), "%sEmail '%s' should be reported as invalid", PAGE_NAME_CONTACT_US,
                email);
        assertEmpty(dialogMessages, "%sNo dialog should appear", PAGE_NAME_CONTACT_US);
    }
}
