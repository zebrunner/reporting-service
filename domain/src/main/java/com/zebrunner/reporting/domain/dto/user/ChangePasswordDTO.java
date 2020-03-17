package com.zebrunner.reporting.domain.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO extends PasswordDTO {

    private static final long serialVersionUID = 6708214365157741315L;

    private String oldPassword;

}
