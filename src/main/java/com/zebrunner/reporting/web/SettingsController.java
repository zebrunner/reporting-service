package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Setting;
import com.zebrunner.reporting.domain.dto.aws.SessionCredentials;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.CryptoService;
import com.zebrunner.reporting.service.SettingsService;
import com.zebrunner.reporting.service.StorageService;
import com.zebrunner.reporting.service.integration.IntegrationService;
import com.zebrunner.reporting.web.documented.SettingDocumentedController;
import com.zebrunner.reporting.web.util.swagger.ApiResponseStatuses;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping(path = "api/settings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SettingsController extends AbstractController implements SettingDocumentedController {

    private final SettingsService settingsService;
    private final CryptoService cryptoService;
    private final IntegrationService integrationService;
    private final StorageService storageService;
    private final RabbitProperties props;

    @Setter(onMethod = @__(@Value("${rabbitmq-public-endpoint}")))
    private String rabbitPublicEndpoint;

    @Setter(onMethod = @__(@Value("${rabbitmq-public-endpoint-port}")))
    private String rabbitPublicEndpointPort;

    @Setter(onMethod = @__(@Value("${elasticsearch.url}")))
    private String elasticSearchUrl;

    @Setter(onMethod = @__(@Value("${elasticsearch.username}")))
    private String elasticSearchUser;

    @Setter(onMethod = @__(@Value("${elasticsearch.password}")))
    private String elasticSearchPassword;

    @GetMapping("tool/{tool}")
    @Override
    public List<Setting> getSettingsByTool(@PathVariable("tool") String typeName) {
        // TODO by nsidorevich on 2019-10-09: refactor and remove
        switch (typeName.toUpperCase()) {
            case "ELASTICSEARCH":
                return elasticSearchSettings();
            case "RABBITMQ":
                return rabbitSettings();
            case "ZEBRUNNER":
                return collectDecryptedIntegrationSettings("ZEBRUNNER");
            default:
                throw new RuntimeException(String.format("Unsupported tool %s, this API should not be used for anything but ElasticSearch or Rabbit", typeName));
        }
    }

    private List<Setting> collectDecryptedIntegrationSettings(String integrationTypeName) {
        Integration integration = integrationService.retrieveDefaultByIntegrationTypeName(integrationTypeName);
        List<Setting> settings =  integration.getSettings()
                                              .stream()
                                              .map(setting -> {
                                                  if (setting.isEncrypted()) {
                                                      String decryptedValue = cryptoService.decrypt(setting.getValue());
                                                      setting.setValue(decryptedValue);
                                                      setting.setEncrypted(false);
                                                  }
                                                  return new Setting(setting.getParam().getName(), setting.getValue());
                                              })
                                              .collect(Collectors.toList());
        settings.add(new Setting(integrationTypeName + "_ENABLED", Boolean.toString(integration.isEnabled())));
        return settings;
    }

    private List<Setting> elasticSearchSettings() {
        return List.of(
                new Setting("URL", elasticSearchUrl),
                new Setting("user", elasticSearchUser),
                new Setting("password", elasticSearchPassword)
        );
    }

    private List<Setting> rabbitSettings() {
        return List.of(
                new Setting("RABBITMQ_HOST", rabbitPublicEndpoint),
                new Setting("RABBITMQ_PORT", rabbitPublicEndpointPort),
                new Setting("RABBITMQ_USER", props.getUsername()),
                new Setting("RABBITMQ_PASSWORD", props.getPassword()),
                new Setting("RABBITMQ_ENABLED", Boolean.TRUE.toString())
        );
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Updates a setting", nickname = "setting", httpMethod = "PUT", response = Setting.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @PreAuthorize("hasPermission('UPDATE_SETTINGS')")
    @PutMapping
    public Setting updateSetting(@RequestBody Setting setting) {
        return settingsService.updateSetting(setting);
    }

    @ApiResponseStatuses
    @ApiOperation(value = "Retrieves a company logo URL", nickname = "getSettingValue", httpMethod = "GET", response = Setting.class)
    @GetMapping("companyLogo")
    public Setting getCompanyLogoURL() {
        return settingsService.getSettingByName("COMPANY_LOGO_URL");
    }

    // TODO by nsidorevich on 2019-10-09: remove this crap

    @ApiOperation(value = "Receives Amazon session credentials", nickname = "getSessionCredentials", httpMethod = "GET", response = SessionCredentials.class)
    @ApiImplicitParams({ @ApiImplicitParam(name = "Authorization", paramType = "header") })
    @GetMapping("amazon/creds")
    public SessionCredentials getSessionCredentials() {
        return storageService.getTemporarySessionCredentials();
    }

}
