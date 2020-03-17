package com.zebrunner.reporting.domain.db.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "arg" })
@XmlRootElement(name = "config")
public class Configuration {
    protected List<Argument> arg;

    public List<Argument> getArg() {
        if (arg == null) {
            arg = new ArrayList<>();
        }
        return this.arg;
    }
}