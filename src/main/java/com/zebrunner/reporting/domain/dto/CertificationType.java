package com.zebrunner.reporting.domain.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Getter
public class CertificationType {

    private Set<String> platforms = new TreeSet<>();
    private Set<String> steps = new TreeSet<>();
    private Map<String, Map<String, String>> screenshots = new HashMap<>();

    public void addScreenshot(String step, String platform, String url) {
        if (step == null || step.isEmpty()) {
            return;
        }

        platforms.add(platform);
        steps.add(step);
        if (!screenshots.containsKey(platform)) {
            screenshots.put(platform, new HashMap<>());
        }
        screenshots.get(platform).put(step, url);
    }

}
