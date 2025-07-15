package com.flowna.app.studySquad.Agents;

public enum Models {

    FLASH_2_5("gemini-2.5-flash-preview-04-17"),
    FLASH_2_5_LIGHT("gemini-2.5-flash-lite-preview-06-17"),
    FLASH_2("gemini-2.0-flash");

    private final String modelName;

    Models(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

}
