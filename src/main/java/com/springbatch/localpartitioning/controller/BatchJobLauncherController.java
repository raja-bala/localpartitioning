package com.springbatch.localpartitioning.controller;

import com.springbatch.localpartitioning.model.ChildJobMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class BatchJobLauncherController {
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @Autowired
    Job childJob;

    @GetMapping("/launch/master/job")
    public String jobLauncherMaster() throws Exception {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            JobParameters jobParameters = new JobParametersBuilder()
//                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            //job launcher is an interface for running the jobs
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return "Job Launched Successfully!";
    }

//    @GetMapping("/launch/child/job")
//    public String jobLauncherChild() throws Exception {
//        Logger logger = LoggerFactory.getLogger(this.getClass());
//        System.out.println("TESTTEST");
//        return "child job";
//    }
    @PostMapping("/launch/child/job")
    public String jobLauncherChild(@RequestBody ChildJobMeta childJobMeta) throws Exception {

//        System.out.println("minValue -" + childJobMeta.getMinValue());
//        System.out.println("maxValue -" + childJobMeta.getMaxValue());
//        System.out.println("partitionId -" + childJobMeta.getPartitionId());

        Logger logger = LoggerFactory.getLogger(this.getClass());
        try {
            JobParameters jobParameters = new JobParametersBuilder()
//                    .addLong("time", System.currentTimeMillis())
                    .addLong("minValue", childJobMeta.getMinValue())
                    .addLong("maxValue", childJobMeta.getMaxValue())
                    .addLong("partitionId", childJobMeta.getPartitionId())
                    .toJobParameters();
            //job launcher is an interface for running the jobs
            jobLauncher.run(childJob, jobParameters);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        return "Job Launched Successfully!";
    }
}
