package io.github.mary2820.utils;

import com.microsoft.playwright.BrowserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

public final class AdBlocker {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdBlocker.class);

    private static final List<String> AD_HOSTS = List.of(
            "googlesyndication.com",
            "doubleclick.net",
            "googleadservices.com",
            "adservice.google.com",
            "fundingchoicesmessages.google.com");

    private AdBlocker() {
    }

    public static void apply(BrowserContext context) {
        context.route(AdBlocker::isAd, route -> {
            LOGGER.debug("Blocked: {}", route.request().url());
            route.abort();
        });
    }

    public static boolean isAd(String url) {
        String host = host(url);
        return host != null && AD_HOSTS.stream()
                .anyMatch(ad -> host.equals(ad) || host.endsWith("." + ad));
    }

    private static String host(String url) {
        try {
            // query/fragment may contain chars URI rejects, e.g. '|' in Google Fonts URLs
            return URI.create(url.split("[?#]", 2)[0]).getHost();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
