package com.example.backend.controller;

import com.example.backend.service.AsyncCustomerUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    private AsyncCustomerUploadService asyncCustomerUploadService;

    @GetMapping("/{jobId}/status")
    public AsyncCustomerUploadService.JobStatus getJobStatus(@PathVariable String jobId) {
        return asyncCustomerUploadService.getJobStatus(jobId);
    }
}
