package com.example.softwareengineer.dto;

import java.time.Instant;

public record JobStatusResponse(
        String jobId,
        String status,
        long processed,
        long inserted,
        long updated,
        long skipped,
        String message,
        Instant startedAt,
        Instant finishedAt
) {
}
