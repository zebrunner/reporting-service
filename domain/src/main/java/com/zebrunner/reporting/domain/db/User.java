package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class User extends AbstractEntity implements Comparable<User> {

    private static final long serialVersionUID = 2720141152633805371L;

    private String username;
    private String email;
    private List<UserPreference> preferences = new ArrayList<>();

    public User() {
    }

    public User(long id) {
        super.setId(id);
    }

    @Override
    public int compareTo(User user) {
        return username.compareTo(user.getUsername());
    }

}
