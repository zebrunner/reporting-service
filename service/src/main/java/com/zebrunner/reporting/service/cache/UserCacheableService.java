package com.zebrunner.reporting.service.cache;

import com.zebrunner.reporting.domain.db.User;

public interface UserCacheableService {

    User getUserByIdTrusted(long id);

    User updateUser(User user);

    User updateStatus(User user);

    User addUserToGroup(User user, long groupId);

    User deleteUserFromGroup(long groupId, long userId);

}
