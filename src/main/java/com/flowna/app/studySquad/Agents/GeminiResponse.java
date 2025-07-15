package com.flowna.app.studySquad.Agents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {

    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;
    private String modelVersion;
    private String responseId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;
        private String finishReason;
        private int index;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageMetadata {
        private int promptTokenCount;
        private int candidatesTokenCount;
        private int totalTokenCount;
        private List<PromptTokensDetail> promptTokensDetails;
        private int thoughtsTokenCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PromptTokensDetail {
        private String modality;
        private int tokenCount;
    }
}

/*
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": ""
          }
        ],
        "role": "model"
      },
      "finishReason": "STOP",
      "index": 0
    }
  ],
  "usageMetadata": {
    "promptTokenCount": 4404,
    "candidatesTokenCount": 12091,
    "totalTokenCount": 24701,
    "promptTokensDetails": [
      {
        "modality": "TEXT",
        "tokenCount": 18
      },
      {
        "modality": "DOCUMENT",
        "tokenCount": 4386
      }
    ],
    "thoughtsTokenCount": 8206
  },
  "modelVersion": "models/gemini-2.5-flash-preview-04-17",
  "responseId": "b1hUaPjaCdXWxN8PydS7iAg"
}
* */