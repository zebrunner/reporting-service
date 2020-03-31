package com.zebrunner.reporting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zebrunner.reporting.persistence.dao.mysql.application.LauncherCallbackMapper;
import com.zebrunner.reporting.domain.db.launcher.LauncherCallback;
import com.zebrunner.reporting.domain.db.launcher.LauncherCallbackResult;
import com.zebrunner.reporting.domain.db.TestRun;
import com.zebrunner.reporting.service.exception.ExternalSystemException;
import com.zebrunner.reporting.service.exception.ProcessingException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.zebrunner.reporting.service.exception.ProcessingException.ProcessingErrorDetail.UNPROCESSABLE_JSON_ENTITY;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.LAUNCHER_CALLBACK_NOT_FOUND;

@Service
public class LauncherCallbackService {

    private static final String ERR_MSG_LAUNCHER_CALLBACK_NOT_FOUND_BY_CI_RUN_ID = "Launcher callback not found by ciRunId '%s'";
    private static final String ERR_MSG_LAUNCHER_CALLBACK_NOT_FOUND_BY_REF = "Launcher callback not found by ref '%s'";

    private static HttpClient httpClient = HttpClient.newHttpClient();

    private final LauncherCallbackMapper launcherCallbackMapper;
    private final TestRunService testRunService;
    private final ObjectMapper mapper;

    public LauncherCallbackService(LauncherCallbackMapper launcherCallbackMapper, TestRunService testRunService, ObjectMapper mapper) {
        this.launcherCallbackMapper = launcherCallbackMapper;
        this.testRunService = testRunService;
        this.mapper = mapper;
    }

    @Transactional()
    public LauncherCallback create(LauncherCallback callback) {
        String ref = generateRef();
        callback.setRef(ref);
        launcherCallbackMapper.create(callback);
        return callback;
    }

    @Transactional(readOnly = true)
    public LauncherCallback retrieveByCiRunId(String ciRunId) {
        LauncherCallback launcherCallback = launcherCallbackMapper.findByCiRunId(ciRunId);
        if (launcherCallback == null) {
            throw new ResourceNotFoundException(LAUNCHER_CALLBACK_NOT_FOUND, String.format(ERR_MSG_LAUNCHER_CALLBACK_NOT_FOUND_BY_CI_RUN_ID, ciRunId));
        }
        return launcherCallback;
    }

    @Transactional(readOnly = true)
    public LauncherCallback retrieveByReference(String ref) {
        LauncherCallback launcherCallback = launcherCallbackMapper.findByRef(ref);
        if (launcherCallback == null) {
            throw new ResourceNotFoundException(LAUNCHER_CALLBACK_NOT_FOUND, String.format(ERR_MSG_LAUNCHER_CALLBACK_NOT_FOUND_BY_REF, ref));
        }
        return launcherCallback;
    }

    @Transactional(readOnly = true)
    public boolean existsByCiRunId(String ciRunId) {
        return launcherCallbackMapper.existsByCiRunId(ciRunId);
    }

    // TODO: 9/13/19 create real launcher callback info response object according future logic
    @Transactional(readOnly = true)
    public TestRun buildInfo(String ref) {
        LauncherCallback callback = retrieveByReference(ref);
        return testRunService.getTestRunByCiRunId(callback.getCiRunId());
    }

    @Transactional(readOnly = true)
    public void notifyOnTestRunFinish(String ciRunId) {
        boolean existsByCiRunId = existsByCiRunId(ciRunId);
        if (existsByCiRunId) {
            LauncherCallback callback = retrieveByCiRunId(ciRunId);
            LauncherCallbackResult result = buildCallbackResult(callback.getCiRunId());
            sendCallback(callback.getUrl(), result);
        }
    }

    @Transactional()
    public void deleteById(Long id) {
        launcherCallbackMapper.deleteById(id);
    }

    private String generateRef() {
        return RandomStringUtils.randomAlphabetic(20);
    }

    private LauncherCallbackResult buildCallbackResult(String ciRunId) {
        TestRun testRun = testRunService.getTestRunByCiRunId(ciRunId);
        String htmlReport = testRunService.exportTestRunHTML(ciRunId);
        return new LauncherCallbackResult(testRun, htmlReport);
    }

    private void sendCallback(String url, LauncherCallbackResult result) {
        String payload = objectToJson(result);
        URI uri = new DefaultUriBuilderFactory(url).builder()
                                                   .build();
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(uri)
                                         .POST(HttpRequest.BodyPublishers.ofString(payload))
                                         .build();
        try {
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            throw new ExternalSystemException(String.format("Cannot to send callback to url '%s'. ", url) + e.getMessage(), e);
        }
    }

    private String objectToJson(Object o) {
        String payload;
        try {
            payload = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new ProcessingException(UNPROCESSABLE_JSON_ENTITY, e.getMessage(), e);
        }
        return payload;
    }
}
