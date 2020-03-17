package com.zebrunner.reporting.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zebrunner.reporting.domain.db.Setting;

/**
 * Created by irina on 23.8.17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConnectedToolType extends AbstractType {

    private static final long serialVersionUID = -5862684391407534486L;

    private String name;
    private List<Setting> settingList;
    private boolean isConnected;

    public List<Setting> getSettingList() {
        return settingList;
    }

    public void setSettingList(List<Setting> settingList) {
        this.settingList = settingList;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
