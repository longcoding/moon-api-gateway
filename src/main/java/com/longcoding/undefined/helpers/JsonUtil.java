package com.longcoding.undefined.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.JsonIOException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * An utility object for creating Json-type objects.
 * The project is currently doing json serialization / deserialization using jackson's objectMapper.
 *
 * @author longcoding
 */

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeFactory typeFactory = TypeFactory.defaultInstance();


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

    public static <T> T readValue(final InputStream stream, Class<T> clazz) {
        try {
            return objectMapper.readValue(stream, clazz);
        } catch (IOException ex) {
            throw new JsonIOException(ex);
        }
    }

    public static Map<String, Object> readValue(final InputStream stream) {
        try {
            return objectMapper.readValue(stream, typeFactory.constructMapType(HashMap.class, String.class, Object.class));
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

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
