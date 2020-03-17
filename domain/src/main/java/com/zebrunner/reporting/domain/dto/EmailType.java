package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class EmailType extends AbstractType {

    private static final long serialVersionUID = 3091393414410237233L;

    @NotEmpty
    private String recipients;
    private String subject;
    private String text;

    public EmailType(String recipients) {
        this.recipients = recipients;
    }

}
