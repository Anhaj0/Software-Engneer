package com.example.softwareengineer.upload;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class UploadJobState {
    private final String jobId;
    private volatile String status;
    private final AtomicLong processed = new AtomicLong();
    private final AtomicLong inserted = new AtomicLong();
    private final AtomicLong updated = new AtomicLong();
    private final AtomicLong skipped = new AtomicLong();
    private volatile String message;
    private final Instant startedAt = Instant.now();
    private volatile Instant finishedAt;

    public UploadJobState(String jobId) {
        this.jobId = jobId;
        this.status = "PROCESSING";
    }

    public void complete(String message) {
        this.status = "COMPLETED";
        this.message = message;
        this.finishedAt = Instant.now();
    }

    public void fail(String message) {
        this.status = "FAILED";
        this.message = message;
        this.finishedAt = Instant.now();
    }

    public String getJobId() { return jobId; }
    public String getStatus() { return status; }
    public AtomicLong getProcessed() { return processed; }
    public AtomicLong getInserted() { return inserted; }
    public AtomicLong getUpdated() { return updated; }
    public AtomicLong getSkipped() { return skipped; }
    public String getMessage() { return message; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
}
