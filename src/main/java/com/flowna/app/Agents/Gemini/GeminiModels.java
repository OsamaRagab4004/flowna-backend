package com.flowna.app.Agents.Gemini;

public enum GeminiModels {

    GEMINI_1_5_PRO("gemini-1.5-pro"),
    GEMINI_2_0_FLASH("gemini-2.0-flash"),
    GEMINI_3_0_ADVANCED("gemini-3.0-advanced");

    private final String modelName;

    GeminiModels(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}
