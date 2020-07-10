package com.zebrunner.reporting.domain.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attribute extends AbstractEntity {
    private static final long serialVersionUID = 6708791122991478693L;

    private String key;
    private String value;

}