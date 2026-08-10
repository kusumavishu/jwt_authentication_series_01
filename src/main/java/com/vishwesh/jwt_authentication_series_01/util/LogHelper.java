package com.vishwesh.jwt_authentication_series_01.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class LogHelper {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // 👈 This fixes the LocalDateTime error
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // Prints dates as readable strings
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static String toPrettyJson(Object object) {
        try {
            if (object == null) return "null";
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            return "Error parsing object: " + e.getMessage();
        }
    }
}
