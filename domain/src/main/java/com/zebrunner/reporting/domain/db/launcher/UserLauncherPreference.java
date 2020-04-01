package com.zebrunner.reporting.domain.db.launcher;

import com.zebrunner.reporting.domain.db.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLauncherPreference extends AbstractEntity {

    private boolean favorite;
}
