package com.flowna.app.studySquad.Agents;
import org.json.JSONArray;
import org.json.JSONObject; // Note: This requires the org.json library
import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class JsonExtractor {

    //HACK

    /**
     * Recursively traverses a JSONObject, sanitizing all of its string values.
     * It iterates through each key-value pair. If a value is a string, it sanitizes it.
     * If a value is a nested JSONObject or JSONArray, it sanitizes them recursively.
     *
     * @param obj The JSONObject to sanitize. Keys will not be modified.
     */
    public static void sanitizeValues(JSONObject obj) {
        // We get the keys and iterate to avoid ConcurrentModificationException.
        String[] keys = JSONObject.getNames(obj);
        if (keys == null) {
            return; // Object is empty
        }

        for (String key : keys) {
            Object value = obj.get(key);

            if (value instanceof String) {
                // Sanitize the string value and update the object.
                obj.put(key, sanitizeForJson((String) value));
            } else if (value instanceof JSONObject) {
                // If the value is another object, sanitize it recursively.
                sanitizeValues((JSONObject) value);
            } else if (value instanceof JSONArray) {
                // If the value is an array, sanitize it recursively.
                sanitizeValues((JSONArray) value);
            }
            // Other types (Number, Boolean, null) do not need sanitization.
        }
    }

    /**
     * Recursively traverses a JSONArray, sanitizing all of its string values.
     * If an element is a nested JSONObject or JSONArray, it sanitizes them recursively.
     *
     * @param arr The JSONArray to sanitize.
     */
    public static void sanitizeValues(JSONArray arr) {
        for (int i = 0; i < arr.length(); i++) {
            Object value = arr.get(i);

            if (value instanceof String) {
                // Sanitize the string value and update the array.
                arr.put(i, sanitizeForJson((String) value));
            } else if (value instanceof JSONObject) {
                // If the value is an object, sanitize it recursively.
                sanitizeValues((JSONObject) value);
            } else if (value instanceof JSONArray) {
                // If the value is an array, sanitize it recursively.
                sanitizeValues((JSONArray) value);
            }
        }
    }

    /**
     * Sanitizes a single string to make it a valid JSON string value. This is the
     * core escaping logic that applies ONLY to string content.
     *
     * @param text The raw string to be sanitized.
     * @return A sanitized string that can be safely embedded as a value in a JSON object.
     */
    private static String sanitizeForJson(String text) {
        if (text == null) {
            return null;
        }

        StringBuilder sanitizedString = new StringBuilder();
        int len = text.length();

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"': sanitizedString.append("\\\""); break;
                case '\\': sanitizedString.append("\\\\"); break;
                case '\b': sanitizedString.append("\\b"); break;
                case '\f': sanitizedString.append("\\f"); break;
                case '\n': sanitizedString.append("\\n"); break;
                case '\r': sanitizedString.append("\\r"); break;
                case '\t': sanitizedString.append("\\t"); break;
                default:
                    if (Character.isISOControl(c)) {
                        sanitizedString.append(String.format("\\u%04x", (int) c));
                    } else {
                        sanitizedString.append(c);
                    }
            }
        }
        return sanitizedString.toString();
    }






}