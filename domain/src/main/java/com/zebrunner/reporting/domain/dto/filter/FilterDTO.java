package com.zebrunner.reporting.domain.dto.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zebrunner.reporting.domain.db.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Getter
@Setter
public class FilterDTO extends AbstractEntity {

    private static final long serialVersionUID = -2497558955789794119L;

    @NotNull(message = "Name required")
    private String name;
    private String description;
    @Valid
    private Subject subject;
    private Long userId;
    private boolean publicAccess;

    public void setSubjectFromString(String subject) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.subject = mapper.readValue(subject, Subject.class);
    }

}
