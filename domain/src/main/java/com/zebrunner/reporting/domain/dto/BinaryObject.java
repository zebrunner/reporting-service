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
    private final String contentType;
    private final String key;
    private final Long size;

    public enum Type {
        ORG_ASSET,
        USER_ASSET,
        APP_PACKAGE
    }

}
