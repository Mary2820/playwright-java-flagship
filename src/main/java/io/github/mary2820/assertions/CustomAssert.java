package io.github.mary2820.assertions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

import java.util.Objects;

import static io.github.mary2820.constants.IConstants.ZERO;

// Playwright assertions take no custom message, so the message is put in front of their own error
public final class CustomAssert {

    private CustomAssert() {
    }

    // for plain values only, a page state needs assertThat, which waits for it
    public static void assertTrue(boolean assertion, String message, Object... args) {
        if (!assertion)
            throw new AssertionError(format(message, args));
    }

    public static void assertFalse(boolean assertion, String message, Object... args) {
        assertTrue(!assertion, message, args);
    }

    public static PageAssert assertThat(Page page, String message, Object... args) {
        return new PageAssert(page, format(message, args));
    }

    public static LocatorAssert assertThat(Locator locator, String message, Object... args) {
        return new LocatorAssert(locator, format(message, args));
    }

    public static void assertEquals(Object actual, Object expected, String message, Object... args) {
        if (!Objects.equals(actual, expected))
            throw new AssertionError(format(message, args) + "\nExpected: " + expected + "\nReceived: " + actual);
    }

    public record PageAssert(Page page, String message) {
        public void hasTitle(String title) {
            verify(message, () -> PlaywrightAssertions.assertThat(page).hasTitle(title));
        }
    }

    public record LocatorAssert(Locator locator, String message) {
        public void isVisible() {
            verify(message, () -> PlaywrightAssertions.assertThat(locator).isVisible());
        }

        public void isHidden() {
            verify(message, () -> PlaywrightAssertions.assertThat(locator).isHidden());
        }

        public void hasCount(int count) {
            verify(message, () -> PlaywrightAssertions.assertThat(locator).hasCount(count));
        }

        public void hasText(String text) {
            verify(message, () -> PlaywrightAssertions.assertThat(locator).hasText(text));
        }

        public void containsText(String text) {
            verify(message, () -> PlaywrightAssertions.assertThat(locator).containsText(text));
        }
    }

    // without args the message stays as is, so a literal % in it cannot break String.format
    private static String format(String message, Object... args) {
        return args.length == ZERO ? message : String.format(message, args);
    }

    // Playwright's error is kept, it carries the expected and the received value
    private static void verify(String message, Runnable assertion) {
        try {
            assertion.run();
        } catch (AssertionError e) {
            throw new AssertionError(message + "\n" + e.getMessage(), e);
        }
    }
}