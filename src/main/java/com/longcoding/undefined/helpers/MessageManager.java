package com.longcoding.undefined.helpers;

import com.longcoding.undefined.configs.YamlProcessor.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * A class for parsing an actual message in an exceptional situation.
 * Use messageSource to get the response message stored in yml.
 *
 * @see com.longcoding.undefined.exceptions.ExceptionType
 *
 * @author longcoding
 */
@Component
@PropertySource(value = {"classpath:message/error-message_en.yml"}, factory = YamlPropertySourceFactory.class)
public class MessageManager {

    @Autowired
    Environment messageSource;

    public String getProperty(String propertyName) { return messageSource.getProperty(propertyName); }

    public int getIntProperty(String propertyName) { return Integer.parseInt(messageSource.getProperty(propertyName)); }

    public boolean getBooleanProperty(String propertyName) { return Boolean.parseBoolean(messageSource.getProperty(propertyName)); }

    public Long getLongProperty(String propertyName) {
        return Long.parseLong(messageSource.getProperty(propertyName));
    }
}
