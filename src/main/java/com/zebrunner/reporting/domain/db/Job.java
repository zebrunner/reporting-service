package com.zebrunner.reporting.domain.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Job extends AbstractEntity {
    private static final long serialVersionUID = -7136622077881406856L;

    private String name;
    private String jobURL;
    private String jenkinsHost;
    private Long automationServerId;
    private User user = new User();

    public Job(String name, String jobURL) {
        this.name = name;
        this.jobURL = jobURL;
    }

    public Job(String name, String jobURL, String jenkinsHost, User user) {
        this.name = name;
        this.jobURL = jobURL;
        this.jenkinsHost = jenkinsHost;
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Job && this.hashCode() == obj.hashCode());
    }

    @Override
    public int hashCode() {
        return (jobURL + user.getUsername() + automationServerId).hashCode();
    }
}
