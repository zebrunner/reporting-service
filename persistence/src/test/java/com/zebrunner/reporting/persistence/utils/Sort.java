package com.zebrunner.reporting.persistence.utils;

import java.util.List;

import com.zebrunner.reporting.domain.db.AbstractEntity;

public class Sort<T extends AbstractEntity> {
    public List<T> sortById(List<T> abstractEntityList) {
        abstractEntityList.sort((o1, o2) -> (int) (o1.getId() - o2.getId()));
        return abstractEntityList;
    }
}