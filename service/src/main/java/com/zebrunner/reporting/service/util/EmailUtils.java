package com.zebrunner.reporting.service.util;

import org.springframework.util.StringUtils;

public class EmailUtils {

    public static String[] obtainRecipients(String recipientsLine) {
        if (StringUtils.isEmpty(recipientsLine)) {
            return new String[] {};
        } else {
            return recipientsLine.trim()
                                 .replaceAll(",", " ")
                                 .replaceAll(";", " ")
                                 .replaceAll("\\[]", " ")
                                 .split(" ");
        }
    }
}
