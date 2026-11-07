package io.github.mary2820.tests.ui;

import io.github.mary2820.base.BaseUiTest;
import io.github.mary2820.factories.TestUserFactory;
import io.github.mary2820.models.TestUser;
import io.github.mary2820.pages.ContactUsPage;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static io.github.mary2820.assertions.CustomAssert.assertThat;
import static io.github.mary2820.constants.IConstants.CONTACT_CONFIRM_DIALOG;
import static io.github.mary2820.constants.IConstants.CONTACT_SUCCESS;
import static io.github.mary2820.constants.IConstants.PAGE_NAME_CONTACT_US;

public class ContactUsTest extends BaseUiTest {
    // a relative path inside the project, an absolute one would break on CI
    private static final Path ATTACHMENT = Paths.get("src/test/resources/files/contact.txt");

    private ContactUsPage contactUs;

    @BeforeMethod
    public void initPages() {
        contactUs = new ContactUsPage(page);
    }

    @Test
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
        // AssertJ without a static import, so it does not clash with the Playwright assertThat
        Assertions.assertThat(dialogMessages).containsExactly(CONTACT_CONFIRM_DIALOG);
    }
}
