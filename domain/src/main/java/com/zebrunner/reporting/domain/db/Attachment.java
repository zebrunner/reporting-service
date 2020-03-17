package com.zebrunner.reporting.domain.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    private String name;
    private File file;
    private String filename;
}