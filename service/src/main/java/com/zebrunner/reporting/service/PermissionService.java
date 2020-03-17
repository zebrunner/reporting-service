package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.PermissionMapper;
import com.zebrunner.reporting.domain.db.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionMapper.getAllPermissions();
    }

}
