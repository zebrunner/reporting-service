package com.zebrunner.reporting.service;

import com.zebrunner.reporting.persistence.dao.mysql.application.UserMapper;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.SearchResult;
import com.zebrunner.reporting.persistence.dao.mysql.application.search.UserSearchCriteria;
import com.zebrunner.reporting.domain.db.Group;
import com.zebrunner.reporting.domain.db.User;
import com.zebrunner.reporting.service.cache.UserCacheableService;
import com.zebrunner.reporting.service.exception.IllegalOperationException;
import com.zebrunner.reporting.service.exception.ResourceNotFoundException;
import com.zebrunner.reporting.service.integration.tool.impl.StorageProviderService;
import com.zebrunner.reporting.service.management.TenancyService;
import com.zebrunner.reporting.service.util.DateTimeUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.jasypt.util.password.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.CHANGE_PASSWORD_IS_NOT_POSSIBLE;
import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.TOKEN_RESET_IS_NOT_POSSIBLE;
import static com.zebrunner.reporting.service.exception.IllegalOperationException.IllegalOperationErrorDetail.USER_CAN_NOT_BE_CREATED;
import static com.zebrunner.reporting.service.exception.ResourceNotFoundException.ResourceNotFoundErrorDetail.USER_NOT_FOUND;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final String ERR_MSG_USER_NOT_FOUND_BY_RESET_TOKEN = "User with such token doesn't exist or is not internal";
    private static final String ERR_MSG_USER_WITH_THIS_ID_DOES_NOT_EXIST = "User with id %d doesn't exist";
    private static final String ERR_MSG_USER_WITH_THIS_USERNAME_DOES_NOT_EXIST = "User with username %s doesn't exist";
    private static final String ERR_MSG_USER_WITH_THIS_EMAIL_DOES_NOT_EXIST = "User with email %s doesn't exist";
    private static final String UNABLE_TO_CHANGE_PASSWORD = "Unable to change password for user %s";
    private static final String ERR_MSG_UNABLE_TO_CREATE_USER_WITH_USERNAME_OR_EMAIL = "Unable to create user with username '%s' or email '%s'";

    @Value("${service.admin.username}")
    private String adminUsername;

    @Value("${service.admin.password}")
    private String adminPassword;

    @Value("${service.admin.group}")
    private String adminGroup;

    @Value("${service.multitenant}")
    private String isMultitenant;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupService groupService;

    @Autowired
    private PasswordEncryptor passwordEncryptor;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private TenancyService tenancyService;

    @Autowired
    private StorageProviderService storageProviderService;

    @Autowired
    private UserCacheableService userCacheableService;

    @PostConstruct
    public void postConstruct() {
        tenancyService.iterateItems(this::initAdmin);
    }

    @Transactional(rollbackFor = Exception.class)
    public void initAdmin() {
        if (!StringUtils.isBlank(adminUsername) && !StringUtils.isBlank(adminPassword)) {
            try {
                User user = getUserByUsername(adminUsername);
                if (user == null) {
                    user = new User(adminUsername);
                    user.setSource(User.Source.INTERNAL);
                    user.setStatus(User.Status.ACTIVE);
                    user.setPassword(passwordEncryptor.encryptPassword(adminPassword));
                    createUser(user);

                    Group group = groupService.getPrimaryGroupByRole(Group.Role.ROLE_ADMIN);// groupService.getGroupByName(adminGroup);
                    addUserToGroup(user, group.getId());
                    user.getGroups().add(group);
                    userPreferenceService.createDefaultUserPreferences(user.getId());
                }
            } catch (Exception e) {
                LOGGER.error("Unable to init admin: " + e.getMessage(), e);
            }
        }
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        return userMapper.getUserById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteProfilePhoto(Long userId) {
        User user = getNotNullUserById(userId);
        storageProviderService.removeFile(user.getPhotoURL());
        user.setPhotoURL(StringUtils.EMPTY);
        updateUser(user);
    }

    @Transactional(readOnly = true)
    public User getUserByIdTrusted(long id) {
        return userCacheableService.getUserByIdTrusted(id);
    }

    @Transactional(readOnly = true)
    public User getNotNullUserById(long id) {
        User user = getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, ERR_MSG_USER_WITH_THIS_ID_DOES_NOT_EXIST, id);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userMapper.getUserByUserName(username);
    }

    @Transactional(readOnly = true)
    public User getDefaultUser() {
        return getNotNullUserByUsername("anonymous");
    }

    @Transactional(readOnly = true)
    public User getNotNullUserByUsername(String username) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, ERR_MSG_USER_WITH_THIS_USERNAME_DOES_NOT_EXIST, username);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        boolean isEmail = new EmailValidator().isValid(usernameOrEmail, null);
        return isEmail ? getUserByEmail(usernameOrEmail) : getUserByUsername(usernameOrEmail);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getNotNullUserByEmail(String email) {
        User user = userMapper.getUserByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, ERR_MSG_USER_WITH_THIS_EMAIL_DOES_NOT_EXIST, email);
        }
        return userMapper.getUserByEmail(email);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginDate(long userId) {
        userMapper.updateLastLoginDate(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createUser(User user) {
        userMapper.createUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateUser(User user) {
        return userCacheableService.updateUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateUserProfile(User user) {
        User dbUser = getNotNullUserById(user.getId());
        dbUser.setFirstName(user.getFirstName());
        dbUser.setLastName(user.getLastName());
        dbUser.setPhotoURL(user.getPhotoURL());
        return updateUser(dbUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserPassword(Long userId, String oldPassword, String password, boolean forceUpdate) {
        User user = getNotNullUserById(userId);
        if (!forceUpdate && (oldPassword == null || !passwordEncryptor.checkPassword(oldPassword, user.getPassword()))) {
            throw new IllegalOperationException(CHANGE_PASSWORD_IS_NOT_POSSIBLE, UNABLE_TO_CHANGE_PASSWORD, user.getUsername());
        }
        updateUserPassword(user, password);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserPassword(User user, String password) {
        user.setPassword(passwordEncryptor.encryptPassword(password));
        updateUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateResetToken(String token, Long userId) {
        userMapper.updateResetToken(token, userId);
    }

    @Transactional(readOnly = true)
    public User getUserByResetToken(String token) {
        User user = userMapper.getUserByResetToken(token);
        if (user == null || !user.getSource().equals(User.Source.INTERNAL)) {
            throw new IllegalOperationException(TOKEN_RESET_IS_NOT_POSSIBLE, ERR_MSG_USER_NOT_FOUND_BY_RESET_TOKEN);
        }
        return user;
    }

    @Transactional
    public User create(User user, Long groupId) {
        boolean exists = isExistByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (exists) {
            throw new IllegalOperationException(USER_CAN_NOT_BE_CREATED, String.format(ERR_MSG_UNABLE_TO_CREATE_USER_WITH_USERNAME_OR_EMAIL, user.getUsername(), user.getEmail()));
        }

        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(passwordEncryptor.encryptPassword(user.getPassword()));
        }
        user.setSource(user.getSource() != null ? user.getSource() : User.Source.INTERNAL);
        user.setStatus(User.Status.ACTIVE);
        createUser(user);
        Group group = recognizeGroupById(groupId);
        if (group != null) {
            addUserToGroup(user, group.getId());
            user.getGroups().add(group);
        }
        userPreferenceService.createDefaultUserPreferences(user.getId());
        return user;
    }

    private Group recognizeGroupById(Long groupId) {
        Group group;
        if (groupId != null) {
            group = groupService.getGroupById(groupId);
        } else {
            group = groupService.getPrimaryGroupByRole(Group.Role.ROLE_USER);
        }
        return group;
    }

    @Transactional
    public User update(User user) {
        boolean exists = isExistById(user.getId());
        if (!exists) {
            throw new ResourceNotFoundException(USER_NOT_FOUND, String.format(ERR_MSG_USER_WITH_THIS_ID_DOES_NOT_EXIST, user.getId()));
        }
        return updateUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User updateStatus(User user) {
        return userCacheableService.updateStatus(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public User addUserToGroup(User user, long groupId) {
        return userCacheableService.addUserToGroup(user, groupId);
    }

    @Transactional(rollbackFor = Exception.class)
    public User deleteUserFromGroup(long groupId, long userId) {
        return userCacheableService.deleteUserFromGroup(groupId, userId);
    }

    @Transactional(readOnly = true)
    public SearchResult<User> searchUsers(UserSearchCriteria sc, Boolean publicDetails) {
        DateTimeUtil.actualizeSearchCriteriaDate(sc);

        List<User> users = userMapper.searchUsers(sc, publicDetails);
        int count = userMapper.getUserSearchCount(sc, publicDetails);

        users.forEach(user -> user.getGroups().removeIf(group -> group.getId() == null));

        return SearchResult.<User>builder()
                .page(sc.getPage())
                .pageSize(sc.getPageSize())
                .sortOrder(sc.getSortOrder())
                .results(users)
                .totalResults(count)
                .build();
    }

    @Transactional(readOnly = true)
    public boolean isExistById(Long id) {
        return userMapper.isExistById(id);
    }

    @Transactional(readOnly = true)
    public boolean isExistByUsernameOrEmail(String username, String email) {
        return userMapper.isExistByUsernameOrEmail(username, email);
    }

    public String getAdminUsername() {
        return this.adminUsername;
    }

}
