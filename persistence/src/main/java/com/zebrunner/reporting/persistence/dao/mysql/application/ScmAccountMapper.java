package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.ScmAccount;

import java.util.List;

public interface ScmAccountMapper {

    void createScmAccount(ScmAccount scmAccount);

    ScmAccount getScmAccountById(Long id);

    ScmAccount getScmAccountByRepo(String repo);

    List<ScmAccount> getAllScmAccounts();

    void updateScmAccount(ScmAccount scmAccount);

    void deleteScmAccountById(Long id);
}
