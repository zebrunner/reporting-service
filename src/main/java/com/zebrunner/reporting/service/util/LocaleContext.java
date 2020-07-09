package com.zebrunner.reporting.service.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.ContextLoader;

public class LocaleContext {

    private static final MessageSource messageSource;

    static {
        messageSource = (MessageSource) ContextLoader.getCurrentWebApplicationContext().getBean("serviceMessageSource");
    }

    public static String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String prefix, String key) {
        return getMessage(prefix + "." + key);
    }

    @SuppressWarnings("rawtypes")
    public static String getMessage(Class classKey) {
        return getMessage("class", classKey.getSimpleName().toLowerCase());
    }
}
