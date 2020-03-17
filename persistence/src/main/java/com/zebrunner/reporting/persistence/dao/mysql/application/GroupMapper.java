package com.zebrunner.reporting.persistence.dao.mysql.application;

import java.util.List;
import java.util.Set;

import com.zebrunner.reporting.domain.db.Group;
import com.zebrunner.reporting.domain.db.Permission;
import org.apache.ibatis.annotations.Param;

public interface GroupMapper {
    void createGroup(Group group);

    void addPermissionsToGroup(@Param("groupId") Long groupId, @Param("permissions") Set<Permission> permissions);

    Group getGroupById(long id);

    Group getGroupByName(String name);

    List<Group> getAllGroups(@Param("publicDetails") boolean publicDetails);

    Group getPrimaryGroupByRole(Group.Role role);

    void updateGroup(Group group);

    void deleteGroup(long id);

    void deletePermissionFromGroup(@Param("groupId") Long groupId, @Param("permissionId") Long permissionId);

    Integer getGroupsCount();
}
