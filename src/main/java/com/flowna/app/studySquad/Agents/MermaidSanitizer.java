package com.flowna.app.studySquad.Agents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MermaidSanitizer {

    /**
     * Sanitizes a Mermaid flowchart string that is provided as a Java string literal
     * with escaped newlines (\\n) and escaped quotes (\\").
     *
     * @param escapedMermaidInput A single-line string containing the Mermaid flowchart
     *                            definition with escaped characters.
     * @return A sanitized string with all HTML tags removed and proper newlines.
     */
    public static String sanitizeEscapedMermaidText(String escapedMermaidInput) {
        if (escapedMermaidInput == null || escapedMermaidInput.isEmpty()) {
            return "";
        }

        // ------------------- NEW (CRUCIAL) STEP -------------------
        // 1. Preprocess the input to convert escaped characters into their actual values.
        //    - Replace literal "\\n" with a real newline character ('\n').
        //    - Replace literal "\\\"" with a real double-quote character ('"').
        String processedInput = escapedMermaidInput.replace("\\n", "\n").replace("\\\"", "\"");
        // ----------------------------------------------------------

        // 2. Remove the Markdown code fences from the now multi-line string
        String textWithoutFences = processedInput.trim().replace("```mermaid", "").replace("```", "").trim();

        // 3. Define a regex pattern to find content within brackets/quotes in a node definition.
        // This pattern works on the now-standard string.
        Pattern nodeContentPattern = Pattern.compile("(\\[\"?|\\(\"?)(.*?)(\"?\\]|\"?\\))");
        Matcher matcher = nodeContentPattern.matcher(textWithoutFences);
        StringBuffer resultBuffer = new StringBuffer();

        // 4. Iterate through all node definitions found
        while (matcher.find()) {
            String nodeContent = matcher.group(2); // The text inside the brackets


            // 6. Clean up extra whitespace for better readability
            String cleanContent = nodeContent.replaceAll("\\s+", " ").trim();

            // 7. Rebuild the node definition with the sanitized content
            matcher.appendReplacement(resultBuffer, Matcher.quoteReplacement(matcher.group(1) + cleanContent + matcher.group(3)));
        }
        matcher.appendTail(resultBuffer);

        return resultBuffer.toString();
    }

}