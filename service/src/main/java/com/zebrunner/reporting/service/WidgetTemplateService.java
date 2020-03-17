package com.zebrunner.reporting.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zebrunner.reporting.persistence.dao.mysql.management.WidgetTemplateMapper;
import com.zebrunner.reporting.domain.db.WidgetTemplate;
import com.zebrunner.reporting.domain.dto.widget.WidgetTemplateParameter;
import com.zebrunner.reporting.service.exception.ForbiddenOperationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.util.SQLExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.WIDGET_TEMPLATE_NOT_FOUND;

@Service
public class WidgetTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetTemplateService.class);

    private static final String ERR_MSG_WIDGET_TEMPLATE_NOT_FOUND = "Widget template with id %s can not be found";

    @Autowired
    private WidgetTemplateMapper widgetTemplateMapper;

    @Autowired
    private SQLExecutor sqlExecutor;

    private ObjectMapper mapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public WidgetTemplate getWidgetTemplateById(Long id) {
        return widgetTemplateMapper.getWidgetTemplateById(id);
    }

    @Transactional(readOnly = true)
    public WidgetTemplate getNotNullWidgetTemplateById(Long id) {
        WidgetTemplate widgetTemplate = widgetTemplateMapper.getWidgetTemplateById(id);
        if (widgetTemplate == null) {
            throw new ResourceNotFoundException(WIDGET_TEMPLATE_NOT_FOUND, ERR_MSG_WIDGET_TEMPLATE_NOT_FOUND, id);
        }
        return widgetTemplate;
    }

    @Transactional(readOnly = true)
    public List<WidgetTemplate> getAllWidgetTemplates() {
        return widgetTemplateMapper.getAllWidgetTemplates();
    }

    public List<WidgetTemplate> getWidgetTemplates() {
        return getAllWidgetTemplates().stream()
                                      .filter(widgetTemplate -> !widgetTemplate.getHidden())
                                      .peek(this::clearRedundantParamsValues)
                                      .collect(Collectors.toList());
    }

    private WidgetTemplate prepareWidgetTemplate(WidgetTemplate widgetTemplate) {
        if (widgetTemplate == null) {
            throw new ForbiddenOperationException("Unable to prepare widget template data");
        }
        executeWidgetTemplateParamsSQLQueries(widgetTemplate);
        return widgetTemplate;
    }

    public void clearRedundantParamsValues(WidgetTemplate widgetTemplate) {
        if (widgetTemplate != null) {
            widgetTemplate.setParamsConfig(processParameters(widgetTemplate.getParamsConfig(), parameter -> {
                if (parameter.getValuesQuery() != null && parameter.getValues() == null) {
                    parameter.setValues(new ArrayList<>());
                }
                parameter.setValuesQuery(null);
            }));
        }
    }

    public WidgetTemplate prepareWidgetTemplateById(Long id) {
        WidgetTemplate widgetTemplate = widgetTemplateMapper.getWidgetTemplateById(id);
        return prepareWidgetTemplate(widgetTemplate);
    }

    private void executeWidgetTemplateParamsSQLQueries(WidgetTemplate widgetTemplate) {
        if (widgetTemplate != null && !StringUtils.isBlank(widgetTemplate.getParamsConfig())) {
            widgetTemplate.setParamsConfig(processParameters(widgetTemplate.getParamsConfig(), this::processParameter));
        }
    }

    private String processParameters(String paramsConfig, Consumer<WidgetTemplateParameter> parameterConsumer) {
        String result = null;
        try {
            Map<String, WidgetTemplateParameter> params = mapper.readValue(paramsConfig, new TypeReference<Map<String, WidgetTemplateParameter>>() {
            });
            params.forEach((name, parameter) -> parameterConsumer.accept(parameter));
            result = mapper.writeValueAsString(params);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    private void processParameter(WidgetTemplateParameter parameter) {
        if (parameter.getValuesQuery() != null) {
            retrieveParameterValues(parameter.getValuesQuery(), parameter);
            // once query is executed it is no longer should be part of response returned to API client
            parameter.setValuesQuery(null);
        }
    }

    private void retrieveParameterValues(String query, WidgetTemplateParameter parameter) {
        List<Object> data = sqlExecutor.getSingleRowResult(query);
        if (data != null) {
            if (parameter.getValues() == null) {
                parameter.setValues(new ArrayList<>());
            }
            parameter.getValues().addAll(data);
        }
    }

}
