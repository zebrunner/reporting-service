package com.zebrunner.reporting.service;

import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.persistence.dao.mysql.application.UserMapper;
import com.zebrunner.reporting.service.cache.UserCacheableService;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.USER_CAN_NOT_BE_CREATED;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.USER_NOT_FOUND;

@Service
public class UserService {

    private static final String ERR_MSG_USER_WITH_THIS_ID_DOES_NOT_EXIST = "User with id %d doesn't exist";
    private static final String ERR_MSG_USER_WITH_THIS_USERNAME_DOES_NOT_EXIST = "User with username %s doesn't exist";
    private static final String ERR_MSG_UNABLE_TO_CREATE_USER_WITH_USERNAME = "Unable to create user with username '%s'";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private UserCacheableService userCacheableService;

    @Transactional(readOnly = true)
    public User getById(long id) {
        return userMapper.getUserById(id);
    }

    @Transactional(readOnly = true)
    public User getNotNullById(long id) {
        User user = getById(id);
        if (user == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, ERR_MSG_USER_WITH_THIS_ID_DOES_NOT_EXIST, id);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public User getDefault() {
        return getNotNullByUsername("anonymous");
    }

    @Transactional(readOnly = true)
    public User getNotNullByUsername(String username) {
        User user = getByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, ERR_MSG_USER_WITH_THIS_USERNAME_DOES_NOT_EXIST, username);
        }
        return user;
    }

    @Transactional
    public User create(User user) {
        boolean exists = existsByUsername(user.getUsername());
        if (exists) {
            throw new IllegalOperationException(USER_CAN_NOT_BE_CREATED, String.format(ERR_MSG_UNABLE_TO_CREATE_USER_WITH_USERNAME, user.getUsername()));
        }
        userPreferenceService.createDefaultUserPreferences(user.getId());
        return user;
    }

    @Transactional(rollbackFor = Exception.class)
    public User update(User user) {
        return userCacheableService.updateUser(user);
    }

    @Transactional(readOnly = true)
    public boolean existById(Long id) {
        return userMapper.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

}
