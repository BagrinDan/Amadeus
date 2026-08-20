package org.example.amadeus.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SettingsManager {
    private static final Logger log = LoggerFactory.getLogger(SettingsManager.class);
    private static final String APP_DIR_NAME = "amadeus";
    private static final String CONFIG_FILE_NAME = "config.json";

    private final ObjectMapper mapper;
    private final File userConfigFile;
    private AppConfig config;

    public SettingsManager() {
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.userConfigFile = resolveUserConfigFile();
        this.config = loadConfig();
    }

    public AppConfig getConfig() {
        return config;
    }

    /**
     * Загружает конфигурацию из файла пользователя (~/.config/config/config.json).
     * Если файла нет — считывает дефолтный config.json из resources и создаёт пользовательский.
     */
    private AppConfig loadConfig() {
        if (userConfigFile.exists()) {
            try {
                log.info("[SettingsManager | INFO] Setup up users config: {}", userConfigFile.getAbsolutePath());
                return mapper.readValue(userConfigFile, AppConfig.class);
            } catch (IOException e) {
                log.error("[SettingsManager | ERROR] Cannot read config.json. Using default settings", e);
            }
        }

        AppConfig defaultConfig = loadDefaultConfigFromResources();

        save(defaultConfig);

        return defaultConfig;
    }

    /**
     * Читает дефолтный config.json из classpath (src/main/resources/config/config.json)
     */
    private AppConfig loadDefaultConfigFromResources() {
        String resourcePath = "/config/config.json";
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is != null) {
                log.info("[SettingsManager | INFO] Upload default settings from resources: {}", resourcePath);
                return mapper.readValue(is, AppConfig.class);
            } else {
                log.warn("[SettingsManager | WARN] Default file not find {} not found in resource, creating empty AppConfig", resourcePath);
            }
        } catch (IOException e) {
            log.error("[SettingsManager | INFO] Cannot read default config file from resource", e);
        }
        return new AppConfig();
    }

    /**
     * Сохраняет текущее состояние объекта AppConfig в ~/.config/amadeus/config.json
     */
    public void save() {
        save(this.config);
    }

    private void save(AppConfig configToSave) {
        try {
            File parentDir = userConfigFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            mapper.writeValue(userConfigFile, configToSave);
            log.info("[SettingsManager | INFO] Config saved in {}", userConfigFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("[SettingsManager | ERROR] Cannot save config in {}", userConfigFile.getAbsolutePath(), e);
        }
    }

    /**
     * Определяет корректный путь к рабочей папке пользователя в зависимости от ОС.
     */
    private File resolveUserConfigFile() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        Path configDir;

        if (os.contains("win")) {
            // Windows: %APPDATA%/amadeus/config.json
            String appData = System.getenv("APPDATA");
            configDir = (appData != null) ? Paths.get(appData, APP_DIR_NAME) : Paths.get(userHome, "AppData", "Roaming", APP_DIR_NAME);
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/amadeus/config.json
            configDir = Paths.get(userHome, "Library", "Application Support", APP_DIR_NAME);
        } else {
            // Linux / Unix: ~/.config/amadeus/config.json
            String xdgConfig = System.getenv("XDG_CONFIG_HOME");
            configDir = (xdgConfig != null) ? Paths.get(xdgConfig, APP_DIR_NAME) : Paths.get(userHome, ".config", APP_DIR_NAME);
        }

        return configDir.resolve(CONFIG_FILE_NAME).toFile();
    }
}