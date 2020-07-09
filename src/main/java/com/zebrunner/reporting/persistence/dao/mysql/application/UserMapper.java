package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.domain.db.User;

public interface UserMapper {

    void createUser(User user);

    User getUserById(long id);

    User getUserByUsername(String username);

    boolean existsById(Long id);

    boolean existsByUsername(String username);

    void updateUser(User user);

    void deleteUserById(long id);

}
