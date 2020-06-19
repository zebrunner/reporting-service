package com.zebrunner.reporting.service.scm;

import com.zebrunner.reporting.domain.db.ScmAccount;
import com.zebrunner.reporting.domain.dto.scm.ScmConfig;
import com.zebrunner.reporting.domain.dto.scm.Organization;
import com.zebrunner.reporting.domain.dto.scm.Repository;
import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.util.GitHubClient;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GitHubService implements IScmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubService.class);

    private final GitHubClient gitHubClient;
    private final CryptoService cryptoService;

    public GitHubService(GitHubClient gitHubClient, CryptoService cryptoService) {
        this.gitHubClient = gitHubClient;
        this.cryptoService = cryptoService;
    }

    public String getAccessToken(String code) {
        return gitHubClient.getAccessToken(code);
    }

    public String getUsername(String token) {
        return gitHubClient.getUsername(token);
    }

    @Override
    public String getLoginName(ScmAccount scmAccount) {
        String result = null;
        GitHub gitHub;
        try {
            gitHub = connectToGitHub(scmAccount);
            result = gitHub.getMyself().getLogin();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<Repository> getRepositories(ScmAccount scmAccount, String organizationName, List<String> existingRepos) throws IOException {
        GitHub gitHub = connectToGitHub(scmAccount);
        GHPerson tokenOwner = gitHub.getMyself();
        GHPerson person = StringUtils.isBlank(organizationName) || tokenOwner.getLogin().equals(organizationName) ?
                gitHub.getMyself() : gitHub.getOrganization(organizationName);
        List<Repository> repositories = person.listRepositories().asList().stream()
                                              .filter(repository -> isRepositoryOwner(person.getLogin(), repository))
                                              .map(GitHubService::mapRepository).collect(Collectors.toList());
        return repositories.stream()
                           .filter(repository -> !existingRepos.contains(repository.getUrl()))
                           .collect(Collectors.toList());
    }

    @Override
    public Repository getRepository(ScmAccount scmAccount) {
        String organizationName = scmAccount.getOrganizationName();
        String repositoryName = scmAccount.getRepositoryName();
        GHRepository repository = null;
        if (!StringUtils.isBlank(organizationName) && !StringUtils.isBlank(repositoryName)) {
            try {
                GitHub gitHub = connectToGitHub(scmAccount);
                String repositoryAbsoluteName = organizationName + "/" + repositoryName;
                repository = gitHub.getRepository(repositoryAbsoluteName);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return repository == null ? null : mapRepository(repository);
    }

    @Override
    public List<Organization> getOrganizations(ScmAccount scmAccount) throws IOException {
        GitHub gitHub = connectToGitHub(scmAccount);
        List<Organization> organizations = gitHub.getMyself().getAllOrganizations().stream().map(organization -> {
            Organization result = new Organization(organization.getLogin());
            result.setAvatarURL(organization.getAvatarUrl());
            return result;
        }).collect(Collectors.toList());
        Organization myself = new Organization(gitHub.getMyself().getLogin());
        myself.setAvatarURL(gitHub.getMyself().getAvatarUrl());
        organizations.add(myself);
        return organizations;
    }

    @Override
    public ScmConfig getScmConfig() {
        return gitHubClient.getConfig();
    }

    @Override
    public ScmAccount.Name getScmAccountName() {
        return gitHubClient.getAccountName();
    }

    private static boolean isRepositoryOwner(String loginName, GHRepository repository) {
        boolean result = false;
        try {
            result = repository.getOwner().getLogin().equals(loginName);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    private static Repository mapRepository(GHRepository repository) {
        Repository repo = new Repository(repository.getName());
        repo.setDefaultBranch(repository.getDefaultBranch());
        repo.setPrivate(repository.isPrivate());
        repo.setUrl(repository.getHtmlUrl().toString());
        return repo;
    }

    private GitHub connectToGitHub(ScmAccount scmAccount) throws IOException {
        String decryptedAccessToken = cryptoService.decrypt(scmAccount.getAccessToken());
        GitHub gitHub;
        switch (scmAccount.getName()) {
            case GITHUB:
                gitHub = GitHub.connectUsingOAuth(decryptedAccessToken);
                break;
            case GITHUB_ENTERPRISE:
                String apiVersion = gitHubClient.getApiVersion();
                gitHub = GitHub.connectToEnterpriseWithOAuth(apiVersion, scmAccount.getLogin(), decryptedAccessToken);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + scmAccount.getName());
        }
        return gitHub;
    }

}
