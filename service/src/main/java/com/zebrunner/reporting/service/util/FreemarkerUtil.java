package com.zebrunner.reporting.service.util;

import com.zebrunner.reporting.service.StorageService;
import com.zebrunner.reporting.service.exception.ProcessingException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.UUID;

import static com.zebrunner.reporting.service.exception.ProcessingException.ProcessingErrorDetail.MALFORMED_FREEMARKER_TEMPLATE;

@AllArgsConstructor
@Component
public class FreemarkerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FreemarkerUtil.class);

    private final Configuration freemarkerConfiguration;
    private final StorageService storageService;

    public String processFreemarkerTemplateFromS3(String key, Object obj) {
        String result;
        try {
            InputStream resource = storageService.get(key);
            Template fTemplate = new Template(UUID.randomUUID().toString(),
                    new InputStreamReader(resource), new Configuration(Configuration.VERSION_2_3_30));
            result = FreeMarkerTemplateUtils.processTemplateIntoString(fTemplate, obj);
        } catch (Exception e) {
            LOGGER.error("Problem with free marker template compilation: " + e.getMessage());
            throw new ProcessingException(MALFORMED_FREEMARKER_TEMPLATE, e.getMessage());
        }
        return result;
    }

    public String getFreeMarkerTemplateContent(String template, Object obj) {
        return getFreeMarkerTemplateContent(template, obj, true);
    }

    /**
     * Precess template through freemarker engine
     * 
     * @param template - path to template file .ftl or string template
     * @param obj - object to process
     * @param isPath - to recognize is template path to .ftl or is a prepared string
     * @return processed template through freemarker engine
     * @throws ProcessingException - on freemarker template compilation
     */
    public String getFreeMarkerTemplateContent(String template, Object obj, boolean isPath) {
        StringBuilder content = new StringBuilder();
        try {
            Template fTemplate = isPath ? freemarkerConfiguration.getTemplate(template)
                    : new Template(UUID.randomUUID().toString(),
                            new StringReader(template), new Configuration(Configuration.VERSION_2_3_30));
            content.append(FreeMarkerTemplateUtils
                    .processTemplateIntoString(fTemplate, obj));
        } catch (Exception e) {
            LOGGER.error("Problem with free marker template compilation: " + e.getMessage());
            throw new ProcessingException(MALFORMED_FREEMARKER_TEMPLATE, e.getMessage());
        }
        return content.toString();
    }
}
