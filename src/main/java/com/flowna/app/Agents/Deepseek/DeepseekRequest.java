package com.flowna.app.Agents.Deepseek;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeepseekRequest {
    private String userPrompt;
    private String systemPrompt;
}