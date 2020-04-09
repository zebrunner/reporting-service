package com.zebrunner.reporting.persistence.dao.mysql.application;

import com.zebrunner.reporting.persistence.dao.mysql.application.search.UserSearchCriteria;
import com.zebrunner.reporting.domain.db.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    void createUser(User user);

    User getUserById(long id);

    User getUserByUserName(String username);

    User getUserByEmail(String email);

    User getUserByResetToken(String token);

    boolean isExistById(Long id);

    boolean isExistByUsernameOrEmail(@Param("username") String username, @Param("email") String email);

    void updateUser(User user);

    void updateStatus(@Param("status") User.Status status, @Param("id") Long id);

    void updateLastLoginDate(long userId);

    void updateResetToken(@Param("resetToken") String resetToken, @Param("id") Long id);

    void deleteUserById(long id);

    void deleteUser(User user);

    void addUserToGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);

    void deleteUserFromGroup(@Param("userId") Long userId, @Param("groupId") Long groupId);

    List<User> searchUsers(@Param("sc") UserSearchCriteria sc, @Param("publicDetails") Boolean publicDetails);

    Integer getUserSearchCount(@Param("sc") UserSearchCriteria sc, @Param("publicDetails") Boolean publicDetails);

}
