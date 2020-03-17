package com.zebrunner.reporting.domain.dto.testsession;

import com.zebrunner.reporting.domain.entity.TestSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchParameter {

    private List<TestSession.Status> statuses;
    private List<String> platforms;
}
