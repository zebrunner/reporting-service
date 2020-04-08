package com.zebrunner.reporting.web.util.dozer;

import org.dozer.DozerConverter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ExpirationLocalDateTimeConverter extends DozerConverter<Integer, LocalDateTime> {

    public ExpirationLocalDateTimeConverter() {
        super(Integer.class, LocalDateTime.class);
    }

    @Override
    public Integer convertFrom(LocalDateTime source, Integer destination) {
        Integer numOfSeconds = null;
        if (source != null) {
            ZoneOffset currentZoneOffset = OffsetDateTime.now().getOffset();
            LocalDateTime currentTime = LocalDateTime.now();
            numOfSeconds = ((Long)(source.toEpochSecond(currentZoneOffset) - currentTime.toEpochSecond(currentZoneOffset))).intValue();
        }
        return numOfSeconds;
    }

    @Override
    public LocalDateTime convertTo(Integer source, LocalDateTime destination) {
        return source != null ? LocalDateTime.now().plusSeconds(source) : null;
    }
}
