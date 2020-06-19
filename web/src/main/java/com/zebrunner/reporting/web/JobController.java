package com.zebrunner.reporting.web;

import com.zebrunner.reporting.domain.db.Job;
import com.zebrunner.reporting.domain.dto.JobDTO;
import com.zebrunner.reporting.domain.dto.JobUrlType;
import com.zebrunner.reporting.service.JobsService;
import com.zebrunner.reporting.service.TestRunService;
import com.zebrunner.reporting.web.documented.JobDocumentedController;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RequestMapping(path = "api/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class JobController extends AbstractController implements JobDocumentedController {

    private final Mapper mapper;
    private final JobsService jobsService;
    private final TestRunService testRunService;

    public JobController(Mapper mapper, JobsService jobsService, TestRunService testRunService) {
        this.mapper = mapper;
        this.jobsService = jobsService;
        this.testRunService = testRunService;
    }

    @PostMapping()
    @Override
    public JobDTO createJob(@RequestBody @Valid JobDTO jobDTO) {
        Job job = mapper.map(jobDTO, Job.class);
        Job updatedJob = jobsService.createOrUpdateJob(job);
        return mapper.map(updatedJob, JobDTO.class);
    }

    @PostMapping("/url")
    @Override
    public JobDTO createJobByUrl(@RequestBody @Valid JobUrlType jobUrl) {
        Long principalId = Long.valueOf(getPrincipalId());
        Job updatedJob = jobsService.createOrUpdateJobByURL(jobUrl.getJobUrlValue(), principalId);
        return mapper.map(updatedJob, JobDTO.class);
    }

    @GetMapping()
    @Override
    public List<Job> getAllJobs() {
        return jobsService.getAllJobs();
    }
}
