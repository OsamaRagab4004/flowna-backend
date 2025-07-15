package com.flowna.app.Agents.Deepseek;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeepseekResponse {
    private String content;
    private int completionTokens;
    private int promptTokens;
    private int totalTokens;
}