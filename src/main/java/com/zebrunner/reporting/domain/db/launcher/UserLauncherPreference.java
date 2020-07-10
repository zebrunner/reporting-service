package com.zebrunner.reporting.domain.db.launcher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLauncherPreference {

    private Long id;
    private boolean favorite;

    public UserLauncherPreference(boolean favorite) {
        this.favorite = favorite;
    }
}
