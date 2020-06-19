package com.zebrunner.reporting.service.scm;

import com.zebrunner.reporting.domain.db.ScmAccount;
import com.zebrunner.reporting.domain.dto.scm.Organization;
import com.zebrunner.reporting.domain.dto.scm.Repository;
import com.zebrunner.reporting.persistence.dao.mysql.application.ScmAccountMapper;
import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.exception.ExternalSystemException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.zebrunner.reporting.service.exception.ExternalSystemException.ExternalSystemErrorDetail.GITHUB_AUTHENTICATION_FAILED;
import static com.zebrunner.reporting.service.exception.ExternalSystemException.ExternalSystemErrorDetail.GITHUB_DEFAULT_BRANCH_IS_NOT_OBTAINED;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.SCM_ACCOUNT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ScmAccountService {

    private static final String ERR_MSG_SCM_ACCOUNT_NOT_FOUND = "SCM account with id %s can not be found";
    private static final String ERR_MSG_SCM_ACCOUNT_NOT_FOUND_FOR_REPO = "SCM account for repo %s can not be found";
    private static final String ERR_MSG_CANNOT_RECOGNIZE_YOUR_AUTHORITY = "Cannot recognize your authority";
    private static final String ERR_MSG_UNABLE_TO_OBTAIN_DEFAULT_BRANCH_NAME = "Unable to obtain scm account default branch name";

    private final ScmAccountMapper scmAccountMapper;
    private final GitHubService gitHubService;
    private final CryptoService cryptoService;

    @Transactional(rollbackFor = Exception.class)
    public ScmAccount createScmAccount(ScmAccount scmAccount) {
        if (scmAccount.getAccessToken() != null && !scmAccount.getAccessToken().isBlank()) {
            String encryptedToken = cryptoService.encrypt(scmAccount.getAccessToken());
            scmAccount.setAccessToken(encryptedToken);
        }
        scmAccountMapper.createScmAccount(scmAccount);
        return scmAccount;
    }

    @Transactional(rollbackFor = Exception.class)
    public ScmAccount createScmAccount(String code) {
        String token = gitHubService.getAccessToken(code);
        if (StringUtils.isEmpty(token)) {
            throw new ExternalSystemException(GITHUB_AUTHENTICATION_FAILED, ERR_MSG_CANNOT_RECOGNIZE_YOUR_AUTHORITY);
        }
        String username = gitHubService.getUsername(token);
        ScmAccount.Name scmAccountName = gitHubService.getScmAccountName();
        ScmAccount scmAccount = new ScmAccount(username, token, scmAccountName);
        return createScmAccount(scmAccount);
    }

    @Transactional(readOnly = true)
    public ScmAccount getScmAccountById(Long id) {
        ScmAccount scmAccount = scmAccountMapper.getScmAccountById(id);
        if (scmAccount == null) {
            throw new ResourceNotFoundException(SCM_ACCOUNT_NOT_FOUND, ERR_MSG_SCM_ACCOUNT_NOT_FOUND, id);
        }
        return scmAccount;
    }

    @Transactional(readOnly = true)
    public ScmAccount getScmAccountByRepo(String repo) {
        ScmAccount scmAccount = scmAccountMapper.getScmAccountByRepo(repo);
        if (scmAccount == null) {
            throw new ResourceNotFoundException(SCM_ACCOUNT_NOT_FOUND, ERR_MSG_SCM_ACCOUNT_NOT_FOUND_FOR_REPO, repo);
        }
        return scmAccount;
    }

    @Transactional(readOnly = true)
    public List<ScmAccount> getAllScmAccounts() {
        return scmAccountMapper.getAllScmAccounts();
    }

    @Transactional(readOnly = true)
    public List<Organization> getScmAccountOrganizations(Long id) throws IOException {
        ScmAccount scmAccount = getScmAccountById(id);
        return gitHubService.getOrganizations(scmAccount);
    }

    @Transactional(readOnly = true)
    public List<Repository> getScmAccountRepositories(Long id, String organizationName) throws IOException {
        ScmAccount scmAccount = getScmAccountById(id);
        List<ScmAccount> allAccounts = getAllScmAccounts();
        List<String> existingRepos = allAccounts.stream()
                                                 .map(ScmAccount::getRepositoryURL)
                                                 .collect(Collectors.toList());
        return gitHubService.getRepositories(scmAccount, organizationName, existingRepos);
    }

    @Transactional(rollbackFor = Exception.class)
    public ScmAccount updateScmAccount(ScmAccount scmAccount) {
        scmAccountMapper.updateScmAccount(scmAccount);
        return scmAccount;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteScmAccountById(Long id) {
        scmAccountMapper.deleteScmAccountById(id);
    }

    @Transactional(readOnly = true)
    public String getDefaultBranch(Long id) {
        ScmAccount scmAccount = getScmAccountById(id);
        Repository repository = gitHubService.getRepository(scmAccount);
        if (repository == null) {
            throw new ExternalSystemException(GITHUB_DEFAULT_BRANCH_IS_NOT_OBTAINED, ERR_MSG_UNABLE_TO_OBTAIN_DEFAULT_BRANCH_NAME);
        }
        return repository.getDefaultBranch();
    }

    public void reEncryptTokens() {
        List<ScmAccount> scmAccounts = getAllScmAccounts();
        scmAccounts.forEach(scmAccount -> {
            String token = scmAccount.getAccessToken();
            String encryptedToken = cryptoService.encrypt(token);
            scmAccount.setAccessToken(encryptedToken);
            updateScmAccount(scmAccount);
        });
    }

}
