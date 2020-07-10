package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.WidgetMapper;
import com.zebrunner.reporting.persistence.utils.SQLTemplateAdapter;
import com.zebrunner.reporting.domain.db.Widget;
import com.zebrunner.reporting.domain.db.WidgetTemplate;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.ProcessingException;
import com.zebrunner.reporting.service.util.FreemarkerUtil;
import com.zebrunner.reporting.service.util.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.WIDGET_CAN_NOT_BE_CREATED;
import static com.zebrunner.reporting.service.exception.ProcessingException.ProcessingErrorDetail.WIDGET_QUERY_EXECUTION_ERROR;

@Service
public class WidgetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetService.class);
    private static final String ERR_MSG_INVALID_CHART_QUERY = "Invalid chart query";
    public static final String ERR_MSG_WIDGET_WITH_SUCH_NAME_ALREADY_EXISTS = "Widget with such name already exists for template %d";

    private final WidgetMapper widgetMapper;
    private final FreemarkerUtil freemarkerUtil;
    private final URLResolver urlResolver;
    private final WidgetTemplateService widgetTemplateService;

    public WidgetService(WidgetMapper widgetMapper, FreemarkerUtil freemarkerUtil, URLResolver urlResolver, WidgetTemplateService widgetTemplateService) {
        this.widgetMapper = widgetMapper;
        this.freemarkerUtil = freemarkerUtil;
        this.urlResolver = urlResolver;
        this.widgetTemplateService = widgetTemplateService;
    }

    public enum DefaultParam {
        SERVICE_URL("serviceUrl"),
        CURRENT_USER_ID("currentUserId"),
        CURRENT_USER_NAME("currentUserName"),
        HASHCODE("hashcode"),
        TEST_CASE_ID("testCaseId");

        private final String parameterName;

        DefaultParam(String parameterName) {
            this.parameterName = parameterName;
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public Widget createWidget(Widget widget) {
        WidgetTemplate widgetTemplate = widget.getWidgetTemplate();
        if (widgetTemplate != null) {
            Widget existingWidget = getWidgetByTitleAndTemplateId(widget.getTitle(), widgetTemplate.getId());
            if (existingWidget != null) {
                throw new IllegalOperationException(WIDGET_CAN_NOT_BE_CREATED, String.format(ERR_MSG_WIDGET_WITH_SUCH_NAME_ALREADY_EXISTS, widgetTemplate.getId()));
            }
            WidgetTemplate preparedWidgetTemplate = prepareWidgetTemplate(widget);
            widget.setType(preparedWidgetTemplate.getType().name());
        }
        widgetMapper.createWidget(widget);
        return widget;
    }

    @Transactional(readOnly = true)
    public Widget getWidgetById(long id) {
        return widgetMapper.getWidgetById(id);
    }

    @Transactional(readOnly = true)
    public Widget getWidgetByTitleAndTemplateId(String title, long templateId) {
        return widgetMapper.getWidgetByTitleAndTemplateId(title, templateId);
    }

    @Transactional(readOnly = true)
    public List<Widget> getAllWidgets() {
        return widgetMapper.getAllWidgets();
    }

    @Transactional(rollbackFor = Exception.class)
    public Widget updateWidget(Widget widget) {
        if (widget.getWidgetTemplate() != null) {
            prepareWidgetTemplate(widget);
        }
        widgetMapper.updateWidget(widget);
        return widget;
    }

    private WidgetTemplate prepareWidgetTemplate(Widget widget) {
        long templateId = widget.getWidgetTemplate().getId();
        WidgetTemplate widgetTemplate = widgetTemplateService.getNotNullWidgetTemplateById(templateId);
        widgetTemplateService.clearRedundantParamsValues(widgetTemplate);
        widget.setWidgetTemplate(widgetTemplate);
        return widgetTemplate;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteWidgetById(Long id) {
        widgetMapper.deleteWidgetById(id);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getQueryResults(Map<String, Object> params, Long templateId, Long userId, String userName) {
        WidgetTemplate widgetTemplate = widgetTemplateService.getNotNullWidgetTemplateById(templateId);
        List<Map<String, Object>> resultList;
        try {
            Map<WidgetService.DefaultParam, Object> additionalParams = new HashMap<>();
            additionalParams.put(WidgetService.DefaultParam.CURRENT_USER_NAME, userName);
            additionalParams.put(WidgetService.DefaultParam.CURRENT_USER_ID, userId);
            resultList = executeSQL(widgetTemplate.getSql(), params, additionalParams, true);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ProcessingException(WIDGET_QUERY_EXECUTION_ERROR, ERR_MSG_INVALID_CHART_QUERY);
        }
        return resultList;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> executeSQL(String sql, Map<String, Object> params, Map<DefaultParam, Object> additionalParams,
                                                boolean isSqlFreemarkerTemplate) {
        if (isSqlFreemarkerTemplate) {
            sql = freemarkerUtil.getFreeMarkerTemplateContent(sql, processDefaultParameters(params, additionalParams), false);
        }
        return widgetMapper.executeSQLTemplate(new SQLTemplateAdapter(sql, params));
    }

    private Map<String, Object> processDefaultParameters(Map<String, Object> params, Map<DefaultParam, Object> additionalParams) {
        Arrays.asList(DefaultParam.values()).forEach(defaultParam -> {
            if (!params.containsKey(defaultParam.parameterName)) {
                params.put(defaultParam.parameterName, getDefaultParamValue(defaultParam, additionalParams));
            }
        });
        return params;
    }

    private Object getDefaultParamValue(DefaultParam param, Map<DefaultParam, Object> additionalParams) {
        Object result = null;
        try {
            switch (param) {
                case SERVICE_URL:
                    result = urlResolver.getServiceURL();
                    break;
                case CURRENT_USER_ID:
                case CURRENT_USER_NAME:
                    result = additionalParams.get(param);
                    break;
                case HASHCODE:
                case TEST_CASE_ID:
                    result = 0;
                    break;
            }
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }
}
