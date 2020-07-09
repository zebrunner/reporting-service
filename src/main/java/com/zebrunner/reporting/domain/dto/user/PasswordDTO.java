package com.zebrunner.reporting.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class PasswordDTO implements Serializable {

    private static final long serialVersionUID = 8483235107118081307L;

    @NotNull
    private Long userId;

    @NotEmpty(message = "Password required")
    @Size(min = 8, max = 50, message = "Too short password")
    @Pattern(regexp = "^[A-Za-z0-9_@!#\"$%&'()*+,-./:;<>=?\\[\\]\\\\^`{}|~]+$")
    protected String password;

}
