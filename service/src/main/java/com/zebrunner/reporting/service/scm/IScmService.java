package com.zebrunner.reporting.service.scm;

import com.zebrunner.reporting.domain.db.ScmAccount;
import com.zebrunner.reporting.domain.dto.scm.ScmConfig;
import com.zebrunner.reporting.domain.dto.scm.Organization;
import com.zebrunner.reporting.domain.dto.scm.Repository;

import java.io.IOException;
import java.util.List;

public interface IScmService {

    ScmConfig getScmConfig();

    ScmAccount.Name getScmAccountName();

    List<Organization> getOrganizations(ScmAccount scmAccount) throws IOException;

    List<Repository> getRepositories(ScmAccount scmAccount, String organizationName, List<String> existingRepos) throws IOException;

    Repository getRepository(ScmAccount scmAccount);

    String getLoginName(ScmAccount scmAccount);

}
