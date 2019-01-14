package com.longcoding.undefined.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonIOException;

import java.io.IOException;

/**
 * Created by longcoding on 19. 1. 4..
 */

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode toJsonNode(final String object) {
        try {
            return objectMapper.readTree(object);
        } catch (IOException ex) {
            throw new JsonIOException(ex);
        }
    }

    public static String fromJson(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException ex) {
            throw new JsonIOException(ex);
        }
    }

    public static <T> T fromJson(JsonNode jsonNode, Class<T> clazz) {
        if (jsonNode == null) {
            return null;
        }

        return fromJson(jsonNode.toString(), clazz);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException ex) {
            throw new JsonIOException(ex);
        }
    }
}
