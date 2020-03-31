package com.zebrunner.reporting.domain.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zebrunner.reporting.domain.db.AbstractEntity;
import com.zebrunner.reporting.domain.db.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class InvitationType extends AbstractEntity {

    @NotNull(message = "{error.email.required}")
    @Email(message = "{error.email.invalid}")
    private String email;

    @NotNull(message = "{error.source.required}")
    private User.Source source;

    @NotNull(message = "{error.group.required}")
    private Long groupId;

    @AssertTrue(message = "{error.email.invalid}")
    @JsonIgnore
    public boolean isEmailConfirmationValid() {
        return this.email == null || new EmailValidator().isValid(this.email, null);
    }

}
