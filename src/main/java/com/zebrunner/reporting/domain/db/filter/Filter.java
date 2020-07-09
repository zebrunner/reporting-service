package com.zebrunner.reporting.domain.db.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zebrunner.reporting.domain.db.AbstractEntity;
import com.zebrunner.reporting.domain.dto.filter.Subject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Filter extends AbstractEntity {
    private static final long serialVersionUID = 5052981349343947449L;

    private String name;
    private String description;
    private String subject;
    private boolean publicAccess;
    private Long userId;

    public void setSubjectFromObject(Subject subject) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.subject = mapper.writeValueAsString(subject);
    }
}
