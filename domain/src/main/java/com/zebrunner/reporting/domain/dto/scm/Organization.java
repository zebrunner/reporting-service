package com.zebrunner.reporting.domain.dto.scm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Organization implements Serializable {

    private static final long serialVersionUID = 5064687126837260732L;

    private String name;
    private String avatarURL;

    public Organization(String name) {
        this.name = name;
    }

}
