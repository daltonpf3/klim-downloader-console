package br.com.daltonpf3.config;

import java.io.InputStream;
import java.util.Properties;

import br.com.daltonpf3.enums.DownloadType;

public class ApplicationPropertiesLoader {

    private static final Properties PROPERTIES = loadProperties();

    // Required properties
    public static final String APP_NAME = getRequiredProperty("app.about.name");
    public static final String APP_VERSION = getRequiredProperty("app.about.version");
    public static final String APP_AUTHOR_NAME = getRequiredProperty("app.about.author-name");
    public static final String APP_REPOSITORY = getRequiredProperty("app.about.repository");

    // Optional properties
    public static final String APP_DOWNLOAD_FOLDER = getPropertyOrDefault("app.config.download-folder", "downloads");
    public static final String APP_AUTO_DOWNLOAD_MP3 = getPropertyOrDefault("app.config.auto-convert-mp3", "n");
    public static final DownloadType APP_DOWNLOAD_TYPE = DownloadType.fromString(getPropertyOrDefault("app.config.download-type", "audio"));

    private static Properties loadProperties() {
        Properties props = new Properties();

        try (InputStream input = ApplicationPropertiesLoader.class.getResourceAsStream("/application.properties")) {
            if (input == null) {
                throw new IllegalStateException("The application.properties file was not found in the classpath.");
            }
            props.load(input);
            return props;
        } catch (Exception ex) {
            throw new ExceptionInInitializerError("Error loading application.properties: " + ex.getMessage());
        }
    }

    private static String getRequiredProperty(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required property in application.properties: " + key);
        }
        return value;
    }

    private static String getPropertyOrDefault(String key, String defaultValue) {
        String value = PROPERTIES.getProperty(key);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value;
    }
}
