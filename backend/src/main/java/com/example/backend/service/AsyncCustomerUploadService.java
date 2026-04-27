package com.example.backend.service;

import com.alibaba.excel.EasyExcel;
import com.example.backend.dto.CustomerExcelModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AsyncCustomerUploadService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // In-memory store for job statuses. (In production, use DB or Redis)
    private final Map<String, JobStatus> jobStatuses = new ConcurrentHashMap<>();

    @Async
    public void processExcelUpload(InputStream inputStream, String jobId) {
        JobStatus status = new JobStatus(jobId);
        jobStatuses.put(jobId, status);
        AtomicInteger processedCount = new AtomicInteger(0);

        try {
            status.setStatus("PROCESSING");
            CustomerUploadListener listener = new CustomerUploadListener(jdbcTemplate, jobId, processedCount);
            
            EasyExcel.read(inputStream, CustomerExcelModel.class, listener).sheet().doRead();
            
            status.setStatus("COMPLETED");
            status.setProcessedCount(processedCount.get());
        } catch (Exception e) {
            status.setStatus("FAILED");
            status.setMessage(e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (Exception ignored) {}
        }
    }

    public JobStatus getJobStatus(String jobId) {
        return jobStatuses.getOrDefault(jobId, new JobStatus(jobId, "UNKNOWN"));
    }

    public static class JobStatus {
        private String jobId;
        private String status;
        private int processedCount;
        private String message;

        public JobStatus(String jobId) {
            this.jobId = jobId;
            this.status = "QUEUED";
        }
        
        public JobStatus(String jobId, String status) {
            this.jobId = jobId;
            this.status = status;
        }

        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getProcessedCount() { return processedCount; }
        public void setProcessedCount(int processedCount) { this.processedCount = processedCount; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
