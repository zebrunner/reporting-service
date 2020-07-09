package com.zebrunner.reporting.persistence.utils;

import com.zebrunner.reporting.domain.db.Attribute;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * SQLAdapter wraps SQL query and parametarizes it with attributes.
 *
 * @author akhursevich
 */
public class SQLAdapter {

    @NotNull
    private String sql;
    private List<Attribute> attributes;

    public SQLAdapter() {
    }

    public SQLAdapter(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }
}