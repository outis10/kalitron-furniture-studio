package com.kalitron.studio.service.dto;

import java.io.Serializable;

public class SketchFieldDTO<T> implements Serializable {

    private T value;

    private String confidence;

    private String sourceText;

    public SketchFieldDTO() {}

    public SketchFieldDTO(T value, String confidence, String sourceText) {
        this.value = value;
        this.confidence = confidence;
        this.sourceText = sourceText;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }
}
