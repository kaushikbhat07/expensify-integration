package com.expensify.integration.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Helper generic class to convert Java Object to a JSON String.
 * @param <T> Java Object
 */
public class ToJson<T> {
    public String toJson(T object) {
        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = null;
        try {
            // Converting the Java object into a JSON string
            jsonStr = Obj.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonStr;
    }
}
