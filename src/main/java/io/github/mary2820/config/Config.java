package io.github.mary2820.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = load();

    private Config() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null)
                throw new IllegalStateException(CONFIG_FILE + " not found on classpath");
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + CONFIG_FILE, e);
        }
        return properties;
    }

    // -Dkey=value overrides the value from config.properties
    private static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank())
            throw new IllegalStateException("Missing config property: " + key);
        return value.trim();
    }

    public static String baseUrl() {
        return get("baseUrl");
    }

    public static String browser() {
        return get("browser");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless"));
    }

    public static double timeout() {
        return Double.parseDouble(get("timeout"));
    }

    public static double slowMo() {
        return Double.parseDouble(get("slowMo"));
    }
}