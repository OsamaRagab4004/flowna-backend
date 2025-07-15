package com.flowna.app.studySquad.Agents;

import com.google.json.JsonSanitizer;

public class JsonFixer {
    /**
     * Fixes a potentially malformed JSON string and removes outer braces if present.
     * This method first uses Google's JsonSanitizer to correct syntax errors,
     * and then strips the outermost curly braces from the result.
     *
     * @param brokenJson The potentially malformed JSON string from the API.
     * @return A sanitized JSON string, potentially without the outermost braces.
     */
    public static String fixJsonString(String brokenJson) {
        if (brokenJson == null || brokenJson.trim().isEmpty()) {
            return brokenJson;
        }
        // First, use the sanitizer to fix the JSON string.
        return JsonSanitizer.sanitize(brokenJson);
    }

    /**
     * Checks if a string starts and ends with curly braces and removes them if true.
     * For example, "{\n  \"topics\": []\n}" becomes "\n  \"topics\": []\n".
     *
     * @param text The input string, potentially a JSON object string.
     * @return The string with the outermost braces removed, or the original string if they were not present.
     */
    public static String stripOuterBraces(String text) {
        if (text == null) {
            return null;
        }
        String trimmedText = text.trim();
        if (trimmedText.startsWith("{") && trimmedText.endsWith("}")) {
            return trimmedText.substring(1, trimmedText.length() - 1);
        }
        return text;
    }



}