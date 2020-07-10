package com.zebrunner.reporting.web.util;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class JMapper {

    private final Map<String, com.googlecode.jmapper.JMapper> identifierToMapper = new ConcurrentHashMap<>();

    public <D, T> D map(T t, Class<D> clazz) {
        String mapperIdentifier = clazz.getCanonicalName() + ":" + t.getClass().getCanonicalName();
        com.googlecode.jmapper.JMapper<D, T> mapper = identifierToMapper
                .computeIfAbsent(mapperIdentifier, s -> new com.googlecode.jmapper.JMapper<>(clazz, t.getClass()));
        return mapper.getDestination(t);
    }

    <D, T> List<D> map(Collection<T> collection, Class<D> clazz) {
        return collection.stream()
                         .map(o -> map(o, clazz))
                         .collect(Collectors.toList());
    }

}
