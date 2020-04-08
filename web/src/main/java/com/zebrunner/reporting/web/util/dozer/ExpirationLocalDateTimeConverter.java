package com.zebrunner.reporting.web.util.dozer;

import org.dozer.DozerConverter;

import java.time.Duration;
import java.time.LocalDateTime;

public class ExpirationLocalDateTimeConverter extends DozerConverter<Integer, LocalDateTime> {

    public ExpirationLocalDateTimeConverter() {
        super(Integer.class, LocalDateTime.class);
    }

    /**
     * Converts a date into the number of seconds
     * left from the date till now.
     *
     * @param source      - expiration date
     * @param destination - number of seconds
     * @return Integer number of seconds or null if source is null
     */
    @Override
    public Integer convertFrom(LocalDateTime source, Integer destination) {
        Integer numOfSeconds = null;
        if (source != null) {
            Duration duration = Duration.between(LocalDateTime.now(), source);
            numOfSeconds = ((Long) duration.toSeconds()).intValue();
        }
        return numOfSeconds;
    }

    /**
     * Converts a number of second to the date
     * coming in the number of seconds.
     *
     * @param source      - number of seconds
     * @param destination - date in the number of seconds
     * @return LocalDateTime date or null if source is null
     */
    @Override
    public LocalDateTime convertTo(Integer source, LocalDateTime destination) {
        return source != null ? LocalDateTime.now().plusSeconds(source) : null;
    }
}
