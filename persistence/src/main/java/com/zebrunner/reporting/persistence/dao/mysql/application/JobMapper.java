package com.zebrunner.reporting.persistence.dao.mysql.application;

import java.util.List;

import com.zebrunner.reporting.domain.db.Job;

public interface JobMapper {
    void createJob(Job job);

    List<Job> getAllJobs();

    Job getJobById(long id);

    Job getJobByName(String name);

    Job getJobByJobURL(String jobURL);

    void updateJob(Job job);

    void deleteJobById(long id);

    void deleteJob(Job job);
}
