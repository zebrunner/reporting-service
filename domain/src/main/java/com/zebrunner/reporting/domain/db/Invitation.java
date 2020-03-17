package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Invitation extends AbstractEntity {

    private static final long serialVersionUID = -7507603908818483927L;

    private String email;
    private String token;
    private User createdBy;
    private Status status;
    private User.Source source;
    private Long groupId;
    private String url;

    public enum Status {
        PENDING,
        ACCEPTED
    }

    public Invitation(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public boolean isValid() {
        return this.status != null && this.getStatus().equals(Status.PENDING);
    }

}
