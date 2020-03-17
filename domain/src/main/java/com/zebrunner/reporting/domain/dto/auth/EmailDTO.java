package com.zebrunner.reporting.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {

    @NotNull(message = "{error.email.required}")
    @Email(message = "{error.email.invalid}")
    private String email;

}
