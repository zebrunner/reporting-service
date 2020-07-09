package com.zebrunner.reporting.service.util;

import org.elasticsearch.search.SearchHit;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ElasticsearchResultHelper {

    private static final String HEADERS_FIELD_NAME = "headers";
    private static final String AMAZON_PATH_FIELD_NAME = "AMAZON_PATH";
    private static final String MESSAGE_FIELD_NAME = "message";

    public static HashMap getHeaders(SearchHit hit) {
        return (HashMap) getSourceMap(hit).get(HEADERS_FIELD_NAME);
    }

    public static String getAmazonPath(SearchHit hit) {
        String result = null;
        HashMap headers = getHeaders(hit);
        if (headers != null) {
            Object amazonPath = headers.get(AMAZON_PATH_FIELD_NAME);
            result = amazonPath != null ? amazonPath.toString() : null;
        }
        return result;
    }

    public static String getMessage(SearchHit hit) {
        Object message = getSourceMap(hit).get(MESSAGE_FIELD_NAME);
        return message != null ? message.toString() : null;
    }

    private static Map<String, Object> getSourceMap(SearchHit hit) {
        return hit.getSourceAsMap();
    }
}
