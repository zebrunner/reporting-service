package com.zebrunner.reporting.domain.db.launcher;

import com.zebrunner.reporting.domain.db.TestRun;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LauncherCallbackResult {

    private TestRun testRun;
    private String htmlReport;
}
