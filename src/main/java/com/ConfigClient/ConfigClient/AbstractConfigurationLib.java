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


@Component
@RefreshScope
@EnableScheduling
public abstract class AbstractConfigurationLib {
    private static final Logger logger = LoggerFactory.getLogger(AbstractConfigurationLib.class);

    @Autowired
    protected Environment environment;


    @Value("${config.refresh.interval:60000}")
    private long refreshInterval;

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
        logger.info("Configuration fetched for {}. Extend this method to process specific properties.", getApplicationName());
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