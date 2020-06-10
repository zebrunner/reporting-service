package com.zebrunner.reporting.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@Getter
@Builder
@RequiredArgsConstructor
public class BinaryObject {

    private final InputStream data;
    private final Type type;
    private final String name;
    private String key;
    private Long size;

    public enum Type {
        ORG_ASSET,
        USER_ASSET,
        SCREENSHOT,
        VIDEO,
        APP_PACKAGE
    }
}
