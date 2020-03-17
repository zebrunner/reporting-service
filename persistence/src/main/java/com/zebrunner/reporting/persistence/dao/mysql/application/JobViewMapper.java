package com.zebrunner.reporting.persistence.dao.mysql.application;

import java.util.List;

import com.zebrunner.reporting.domain.db.JobView;
import org.apache.ibatis.annotations.Param;

public interface JobViewMapper {
    void createJobView(JobView jobView);

    JobView getJobViewById(long id);

    List<JobView> getJobViewsByViewId(@Param("viewId") long viewId);

    List<JobView> getJobViewsByViewIdAndEnv(@Param("viewId") long viewId, @Param("env") String env);

    void deleteJobViewById(long id);

    void deleteJobViewsByViewIdAndEnv(@Param("viewId") long viewId, @Param("env") String env);
}