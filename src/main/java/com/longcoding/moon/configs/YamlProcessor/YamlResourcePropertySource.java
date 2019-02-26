package com.longcoding.moon.configs.YamlProcessor;

import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;

public class YamlResourcePropertySource extends PropertiesPropertySource {
    public YamlResourcePropertySource(String name, EncodedResource resource) throws IOException {
        super(name, new YamlPropertiesProcessor(resource.getResource()).createProperties());
    }
}
