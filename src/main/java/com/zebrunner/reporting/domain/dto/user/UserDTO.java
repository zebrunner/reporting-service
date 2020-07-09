package com.zebrunner.reporting.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.zebrunner.reporting.domain.db.UserPreference;
import com.zebrunner.reporting.domain.dto.AbstractType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserDTO extends AbstractType {
    private static final long serialVersionUID = -6663692781158665080L;

    @NotEmpty(message = "Username required")
    @Pattern(regexp = "[\\w-]+", message = "Invalid format")
    private String username;
    private String email;
    private List<UserPreference> preferences = new ArrayList<>();

}