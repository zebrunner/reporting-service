package com.zebrunner.reporting.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zebrunner.reporting.domain.db.ScmAccount;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScmAccountDTO extends AbstractType {

    private static final long serialVersionUID = 9120645976990419377L;

    private String apiVersion;
    private String organizationName;
    private String repositoryName;
    private String avatarURL;
    private String repositoryURL;
    private Long userId;
    private ScmAccount.Name name;
    private String defaultBranch;

}
