package com.example.softwareengineer.controller;

import com.example.softwareengineer.service.CustomerUploadService;
import com.example.softwareengineer.service.JobService;
import com.example.softwareengineer.upload.UploadJobState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class UploadController {

    private final JobService jobService;
    private final CustomerUploadService customerUploadService;

    public UploadController(JobService jobService, CustomerUploadService customerUploadService) {
        this.jobService = jobService;
        this.customerUploadService = customerUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.getOriginalFilename() == null || !file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only .xlsx files are supported"));
        }
        UploadJobState job = jobService.createJob();
        customerUploadService.processAsync(file.getBytes(), job);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("jobId", job.getJobId()));
    }
}
