package com.expensify.integration.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
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
