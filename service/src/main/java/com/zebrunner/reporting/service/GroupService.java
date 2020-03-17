package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.GroupMapper;
import com.zebrunner.reporting.domain.db.Group;
import com.zebrunner.reporting.domain.db.Permission;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.GROUP_CAN_NOT_BE_DELETED;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.GROUP_NOT_FOUND;

@Service
public class GroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupService.class);

    private static final String ERR_MSG_GROUP_CAN_NOT_BE_FOUND = "Group with id %s can not be found";

    private final GroupMapper groupMapper;

    public GroupService(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    @CachePut(value = "groups", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #group.id")
    @Transactional(rollbackFor = Exception.class)
    public Group createGroup(Group group) {
        groupMapper.createGroup(group);
        addPermissionsToGroup(group);
        return group;
    }

    @CachePut(value = "groups", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #group.id")
    @Transactional(rollbackFor = Exception.class)
    public Group addPermissionsToGroup(Group group) {
        Group dbGroup = groupMapper.getGroupById(group.getId());

        Set<Permission> intersection = new HashSet<>(group.getPermissions());
        intersection.retainAll(dbGroup.getPermissions());

        dbGroup.getPermissions().removeAll(intersection);
        group.getPermissions().removeAll(intersection);
        dbGroup.getPermissions().forEach(permission -> {
            try {
                deletePermissionFromGroup(group.getId(), permission.getId());
                // TODO by nsidorevich on 2019-09-03: ???
            } catch (RuntimeException e) {
                LOGGER.error(e.getMessage());
            }
        });
        groupMapper.addPermissionsToGroup(group.getId(), group.getPermissions());
        group.getPermissions().addAll(intersection);
        return group;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "groups", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #id")
    public Group getGroupById(long id) {
        Group group = groupMapper.getGroupById(id);
        if (group == null) {
            throw new ResourceNotFoundException(GROUP_NOT_FOUND, ERR_MSG_GROUP_CAN_NOT_BE_FOUND, id);
        }
        return group;
    }

    @Transactional(readOnly = true)
    public Group getGroupByName(String name) {
        return groupMapper.getGroupByName(name);
    }

    @Transactional(readOnly = true)
    public Group getPrimaryGroupByRole(Group.Role role) {
        return groupMapper.getPrimaryGroupByRole(role);
    }

    @Transactional(readOnly = true)
    public List<Group> getAllGroups(Boolean isPublic) {
        List<Group> groupList = groupMapper.getAllGroups(isPublic);
        for (Group group : groupList) {
            Collections.sort(group.getUsers());
        }
        return groupList;
    }

    public static List<Group.Role> getRoles() {
        return Arrays.asList(Group.Role.values());
    }

    @Transactional(readOnly = true)
    public Integer getGroupsCount() {
        return groupMapper.getGroupsCount();
    }

    @CachePut(value = "groups", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #group.id")
    @Transactional(rollbackFor = Exception.class)
    public Group updateGroup(Group group) {
        groupMapper.updateGroup(group);
        addPermissionsToGroup(group);
        return group;
    }

    @CacheEvict(value = "groups", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #id")
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(long id) {
        Group group = getGroupById(id);
        if (group.getUsers().size() > 0) {
            throw new IllegalOperationException(GROUP_CAN_NOT_BE_DELETED, "It's necessary to clear the group initially.");
        }
        groupMapper.deleteGroup(id);
    }

    @CacheEvict(value = "groups", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #groupId")
    @Transactional(rollbackFor = Exception.class)
    public void deletePermissionFromGroup(long groupId, long permissionId) {
        groupMapper.deletePermissionFromGroup(groupId, permissionId);
    }
}