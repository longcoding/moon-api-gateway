package com.longcoding.undefined.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonIOException;

import java.io.IOException;

/**
 * Created by longcoding on 19. 1. 4..
 */

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode toJson(final Object object) {
        return objectMapper.valueToTree(object);
    }

    public static String fromJson(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException ex) {
            throw new JsonIOException(ex);
        }
    }
}
