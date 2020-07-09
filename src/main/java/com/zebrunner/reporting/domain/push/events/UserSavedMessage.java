package com.zebrunner.reporting.domain.push.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSavedMessage {

    private String tenantName;

    private Integer id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private String source;
    private String status;
    private Set<Integer> groupIds;
    private Set<String> permissions;

}
