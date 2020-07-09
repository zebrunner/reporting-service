package com.zebrunner.reporting.service.cache.impl;

import com.zebrunner.reporting.persistence.dao.mysql.application.UserMapper;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.service.cache.UserCacheableService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCacheableServiceImpl implements UserCacheableService {

    private static final String USER_CACHE_NAME = "users";

    private final UserMapper userMapper;

    public UserCacheableServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = USER_CACHE_NAME, condition = "#id != null", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #id")
    public User getUserByIdTrusted(long id) {
        return userMapper.getUserById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = USER_CACHE_NAME, condition = "#user.id != null", key = "new com.zebrunner.reporting.persistence.utils.TenancyContext().getTenantName() + ':' + #user.id")
    public User updateUser(User user) {
        userMapper.updateUser(user);
        return user;
    }

}
