package com.zebrunner.reporting.domain.db;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Permission extends AbstractEntity implements Comparable<Permission> {

    private static final long serialVersionUID = -3347361010220589543L;

    private Name name;
    private Block block;

    public Permission() {
    }

    public Permission(Name name) {
        this.name = name;
    }

    public enum Block {
        DASHBOARDS,
        TEST_RUNS,
        TEST_RUN_VIEWS,
        TEST_SESSIONS,
        INVITATIONS,
        USERS,
        PROJECTS,
        INTEGRATIONS,
        LAUNCHERS,
        BILLING
    }

    public enum Name {
        INVITE_USERS,
        MODIFY_INVITATIONS,
        VIEW_HIDDEN_DASHBOARDS,
        MODIFY_DASHBOARDS,
        MODIFY_WIDGETS,
        MODIFY_TEST_RUNS,
        TEST_RUNS_CI,
        MODIFY_TEST_RUN_VIEWS,
        VIEW_TEST_RUN_VIEWS,
        MODIFY_TESTS,
        MODIFY_USERS,
        VIEW_USERS,
        MODIFY_USER_GROUPS,
        MODIFY_PROJECTS,
        MODIFY_INTEGRATIONS,
        VIEW_INTEGRATIONS,
        MODIFY_LAUNCHERS,
        VIEW_LAUNCHERS,
        PAYMENT_METHODS,
        INVOICES,
        REFRESH_TOKEN
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof Permission) {
            if (this.getId() != null) {
                equals = this.getId().equals(((Permission) o).getId());
            }
            if (this.getName() != null) {
                equals = this.getName().name().equals(((Permission) o).getName().name());
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return this.getId() != null ? this.getId().intValue() : this.getName().name().hashCode();
    }

    @Override
    public int compareTo(Permission o) {
        return o != null && this.getId() > o.getId() ? 1 : -1;
    }
}
