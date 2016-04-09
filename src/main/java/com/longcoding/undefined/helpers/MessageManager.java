package com.longcoding.undefined.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by longcoding on 16. 4. 9..
 */
@Component
@PropertySource({"classpath:default-undefined.properties", "classpath:default-errormessage.properties"})
public class MessageManager {

    @Autowired
    Environment messageSource;

    public String getProperty(String propertyName) {
        return messageSource.getProperty(propertyName);
    }

    public int getIntProperty(String propertyName) {
        return Integer.parseInt(messageSource.getProperty(propertyName));
    }

    public boolean getBooleanProperty(String propertyName) {
        return Boolean.parseBoolean(messageSource.getProperty(propertyName));
    }

    public Long getLongProperty(String propertyName) {
        return Long.parseLong(messageSource.getProperty(propertyName));
    }
}
