package com.zebrunner.reporting.domain.dto.aws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

// TODO by nsidorevich on 6/10/20: this needs to be gone
@Getter
@Setter
@AllArgsConstructor
public class FileUploadType {

    private final InputStream inputStream;
    private final Type type;
    private final String fileName;
    private final long fileSize;

    public enum Type {
        USERS("/users"),
        COMMON("/common"),
        VIDEOS("/artifacts/videos"),
        SCREENSHOTS("/artifacts/screenshots"),
        APP("/artifacts/applications");

        private final String path;

        Type(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

}
