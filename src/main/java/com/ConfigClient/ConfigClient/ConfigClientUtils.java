package com.ConfigClient.ConfigClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class ConfigClientUtils {

    @Value("${app.message:Default Message}")
    private String message;

    public String getMessage() {
        return message;
    }
}