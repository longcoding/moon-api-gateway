package com.longcoding.undefined.configs.YamlProcessor;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class YamlPropertySourceFactory implements PropertySourceFactory {
    private static final String YML_FILE_EXTENSION = ".yml";

    @Override public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        String filename = resource.getResource().getFilename();

        if (filename != null && filename.endsWith(YML_FILE_EXTENSION)) {
            return name != null ? new YamlResourcePropertySource(name, resource) : new YamlResourcePropertySource(getNameForResource(resource.getResource()), resource);
        }

        return (name != null ? new ResourcePropertySource(name, resource) : new ResourcePropertySource(resource));
    }


    private String getNameForResource(Resource resource) {
        String name = resource.getDescription();

        if (!StringUtils.hasText(name)) {
            name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
        }

        return name;
    }
}
