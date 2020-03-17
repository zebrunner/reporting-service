package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.Setting;

import java.util.List;

public interface SettingsMapper {
    void createSetting(Setting setting);

    Setting getSettingById(long id);

    Setting getSettingByName(String name);

    //List<Setting> getSettingsByTool(Tool tool);

    //List<Setting> getSettingsByEncrypted(boolean isEncrypted);

    List<Setting> getAllSettings();

    //List<Setting> getSettingsByIntegration(@Param("isIntegrationTool") boolean isIntegrationTool);

    //List<Tool> getTools();

    void updateSetting(Setting setting);

    void updateIntegrationSetting(Setting setting);

    void deleteSetting(Setting setting);

    void deleteSettingById(long id);

    String getPostgresVersion();
}
