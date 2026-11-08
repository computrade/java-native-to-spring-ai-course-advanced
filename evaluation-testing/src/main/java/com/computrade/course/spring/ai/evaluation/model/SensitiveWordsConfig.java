package com.computrade.course.spring.ai.evaluation.model;

import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

@Data
public class SensitiveWordsConfig {

    // Getters and Setters
    private List<String> violence;
    private List<String> hateSpeech;
    private List<String> illegal;
    private List<String> injection;
    private List<String> pii;

    // Helper to flatten all categories into a single list
    public List<String> getAllWords() {
        return Stream.of(violence, hateSpeech, illegal, injection, pii)
                .filter(java.util.Objects::nonNull)
                .flatMap(List::stream)
                .toList();
    }
}
