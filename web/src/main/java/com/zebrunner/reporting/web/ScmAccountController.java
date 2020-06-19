package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.ScmAccount;
import com.zebrunner.reporting.domain.dto.ScmAccountDTO;
import com.zebrunner.reporting.domain.dto.scm.Organization;
import com.zebrunner.reporting.domain.dto.scm.Repository;
import com.zebrunner.reporting.domain.dto.scm.ScmConfig;
import com.zebrunner.reporting.service.scm.GitHubService;
import com.zebrunner.reporting.service.scm.ScmAccountService;
import com.zebrunner.reporting.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Api("SCM accounts API")
@CrossOrigin
@RequestMapping(path = "api/scm", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class ScmAccountController extends AbstractController {

    private final ScmAccountService scmAccountService;
    private final GitHubService gitHubService;
    private final Mapper mapper;

    @ApiResponseStatuses
    @ApiOperation(value = "Creates an SCM account", nickname = "createScmAccount", httpMethod = "POST", response = ScmAccountDTO.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @PostMapping("/accounts")
    public ScmAccountDTO createScmAccount(@Valid @RequestBody ScmAccountDTO scmAccountDTO) {
        ScmAccount scmAccount = mapper.map(scmAccountDTO, ScmAccount.class);
        scmAccount = scmAccountService.createScmAccount(scmAccount);
        return mapper.map(scmAccount, ScmAccountDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves an SCM account by its id", nickname = "getScmAccountById", httpMethod = "GET", response = ScmAccountDTO.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/accounts/{id}")
    public ScmAccountDTO getScmAccountById(@PathVariable("id") Long id) {
        ScmAccount scmAccount = scmAccountService.getScmAccountById(id);
        return mapper.map(scmAccount, ScmAccountDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves all SCM accounts", nickname = "getAllScmAccounts", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping("/accounts")
    public List<ScmAccountDTO> getAllScmAccounts() {
        List<ScmAccount> scmAccounts = scmAccountService.getAllScmAccounts();
        return scmAccounts.stream()
                          .map(scmAccount -> mapper.map(scmAccount, ScmAccountDTO.class))
                          .collect(Collectors.toList());
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves the default branch of an SCM account by its id", nickname = "getScmAccountDefaultBranch", httpMethod = "GET", response = String.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS', 'VIEW_LAUNCHERS')")
    @GetMapping(value = "/accounts/{id}/defaultBranch", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getScmAccountDefaultBranch(@PathVariable("id") long id) {
        return scmAccountService.getDefaultBranch(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Updates an SCM account", nickname = "updateScmAccount", httpMethod = "PUT", response = ScmAccountDTO.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @PutMapping("/accounts")
    public ScmAccountDTO updateScmAccount(@RequestBody @Valid ScmAccountDTO scmAccountDTO) {
        long scmAccountId = scmAccountDTO.getId();
        ScmAccount account = scmAccountService.getScmAccountById(scmAccountId);
        ScmAccount currentAccount = mapper.map(scmAccountDTO, ScmAccount.class);
        if (account.getUserId() == null || account.getUserId() <= 0) {
            currentAccount.setUserId(getPrincipalId());
        }
        currentAccount = scmAccountService.updateScmAccount(currentAccount);
        return mapper.map(currentAccount, ScmAccountDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Deletes an SCM account by its id", nickname = "deleteScmAccountById", httpMethod = "DELETE")
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @DeleteMapping("/accounts/{id}")
    public void deleteScmAccountById(@PathVariable("id") Long id) {
        scmAccountService.deleteScmAccountById(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves the Github config", nickname = "getScmConfig", httpMethod = "GET", response = ScmConfig.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @GetMapping(path = "github/config")
    public ScmConfig getScmConfig() {
        return gitHubService.getScmConfig();
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Github callback", nickname = "callback", httpMethod = "GET", response = ScmAccountDTO.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/github/exchange")
    public ScmAccountDTO authorizeCallback(@RequestParam("code") String code) {
        ScmAccount scmAccount = scmAccountService.createScmAccount(code);
        return mapper.map(scmAccount, ScmAccountDTO.class);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves all organizations", nickname = "getOrganizations", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/github/organizations/{scmId}")
    public List<Organization> getOrganizations(@PathVariable("scmId") Long id) throws IOException {
        return scmAccountService.getScmAccountOrganizations(id);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves all repositories", nickname = "getRepositories", httpMethod = "GET", response = List.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "Authorization", paramType = "header")})
    @PreAuthorize("hasAnyPermission('MODIFY_LAUNCHERS')")
    @GetMapping("/github/repositories/{scmId}")
    public List<Repository> getRepositories(@PathVariable("scmId") Long id,
                                            @RequestParam(name = "org", required = false) String organizationName) throws IOException {
        return scmAccountService.getScmAccountRepositories(id, organizationName);
    }

}