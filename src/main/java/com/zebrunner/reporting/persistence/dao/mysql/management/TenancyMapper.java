package com.zebrunner.reporting.persistence.dao.mysql.management;

import com.zebrunner.reporting.domain.db.Tenancy;

import java.util.List;

public interface TenancyMapper {

    List<Tenancy> getAllTenancies();

    Tenancy getTenancyByName(String name);
}
