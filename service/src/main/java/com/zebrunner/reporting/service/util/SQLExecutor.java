package com.zebrunner.reporting.service.util;

import com.zebrunner.reporting.persistence.dao.mysql.application.WidgetMapper;
import com.zebrunner.reporting.persistence.utils.SQLAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SQLExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLExecutor.class);

    // TODO: switch to generic mapper or raw JDBC api
    private final WidgetMapper widgetMapper;

    public SQLExecutor(WidgetMapper widgetMapper) {
        this.widgetMapper = widgetMapper;
    }

    /**
     * Returns result map if query is valid or single result with key == null on sql is invalid
     * 
     * @param sql - sql query
     * @return result map
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMultiRowResult(String sql) {
        List<Map<String, Object>> result = null;
        if (sql != null) {
            try {
                result = widgetMapper.executeSQL(new SQLAdapter(sql));
            } catch (Exception e) {
                result = new ArrayList<>();
                LOGGER.debug("String starts with 'select' but is not" + " sql or is not valid: '" + sql + "'");
            }
        }
        return result;
    }

    /**
     * Returns result list for single row result if query is valid or one item list on sql is invalid
     * 
     * @param sql - sql query
     * @return - result list
     */
    public List<Object> getSingleRowResult(String sql) {
        List<Map<String, Object>> multiRowResult = getMultiRowResult(sql);
        List<Object> result = new ArrayList<>();
        if (multiRowResult != null && !multiRowResult.isEmpty() && multiRowResult.get(0).keySet().size() == 1) {
            multiRowResult.forEach(resultItem -> {
                if (resultItem != null) {
                    for (String key : resultItem.keySet()) {
                        result.add(resultItem.get(key));
                    }
                }
            });
        }
        return result;
    }

}
