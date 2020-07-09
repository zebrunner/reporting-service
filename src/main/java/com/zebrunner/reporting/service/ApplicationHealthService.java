package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.repository.IntegrationRepository;
import com.zebrunner.reporting.domain.entity.integration.Integration;
import com.zebrunner.reporting.service.exception.ExternalSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.zebrunner.reporting.service.exception.ExternalSystemException.ExternalSystemErrorDetail.POSTGRES_VERSION_CAN_NOT_BE_FOUND;

@Service
public class ApplicationHealthService {

    private static final String ERR_MSG_POSTGRES_VERSION_NOT_FOUND = "Unable to retrieve Postgres version";

    private final SettingsService settingsService;
    private final IntegrationRepository integrationRepository;

    public ApplicationHealthService(SettingsService settingsService, IntegrationRepository integrationRepository) {
        this.settingsService = settingsService;
        this.integrationRepository = integrationRepository;
    }

    @Transactional(readOnly = true)
    public String getStatus() {
        String version = settingsService.getPostgresVersion();
        if (StringUtils.isEmpty(version)) {
            throw new ExternalSystemException(POSTGRES_VERSION_CAN_NOT_BE_FOUND, ERR_MSG_POSTGRES_VERSION_NOT_FOUND);
        }
        Integration firstIntegration = integrationRepository.findById(1L)
                                                            .orElse(null);
        return String.format("Service is up and running. Integration: %s", firstIntegration);
    }
}
