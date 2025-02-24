package com.ConfigClient.ConfigClient;


import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
@RefreshScope
@EnableScheduling
public abstract class AbstractwithField {

    private static final Logger logger = LoggerFactory.getLogger(AbstractwithField.class);

    @Autowired
    protected Environment environment;

    // Refresh interval (default 60 seconds)
    @Value("${config.refresh.interval:60000}")
    private long refreshInterval;

    // Cache for field values
    private final Map<String, Object> configCache = new HashMap<>();

    @PostConstruct
    public void init() {
        logger.info("Initializing configuration for application: {}", getApplicationName());
        fetchConfiguration();
    }

    @Scheduled(fixedRateString = "${config.refresh.interval:60000}")
    public void refreshConfiguration() {
        logger.info("Refreshing configuration for application: {}", getApplicationName());
        fetchConfiguration();
    }

    protected void fetchConfiguration() {
        logger.info("Fetching all @Value-annotated fields for {}", getApplicationName());
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Value.class)) {
                field.setAccessible(true);
                String propertyKey = field.getAnnotation(Value.class).value();
                // Strip ${} and default value (e.g., "${app.message:default}" -> "app.message")
                String key = propertyKey.replaceAll("\\$\\{(.*?)\\}", "$1").split(":")[0];
                String value = environment.getProperty(key);
                try {
                    field.set(this, value != null ? convertValue(value, field.getType()) : null);
                    configCache.put(key, field.get(this));
                    logger.info("  {} = {}", key, field.get(this));
                } catch (IllegalAccessException e) {
                    logger.error("Failed to set field {}: {}", key, e.getMessage());
                }
            }
        }
    }

    // Convert string value to field type
    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType == String.class) return value;
        if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(value);
        if (targetType == Long.class || targetType == long.class) return Long.parseLong(value);
        if (targetType == Boolean.class || targetType == boolean.class) return Boolean.parseBoolean(value);
        return value; // Default to string if type unknown
    }

    protected abstract String getApplicationName();

    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }
}