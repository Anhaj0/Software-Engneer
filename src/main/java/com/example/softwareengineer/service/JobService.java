package com.example.softwareengineer.service;

import com.example.softwareengineer.dto.JobStatusResponse;
import com.example.softwareengineer.upload.UploadJobState;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JobService {

    private final Map<String, UploadJobState> jobs = new ConcurrentHashMap<>();

    public UploadJobState createJob() {
        String id = UUID.randomUUID().toString();
        UploadJobState state = new UploadJobState(id);
        jobs.put(id, state);
        return state;
    }

    public Optional<JobStatusResponse> getJob(String jobId) {
        UploadJobState state = jobs.get(jobId);
        if (state == null) {
            return Optional.empty();
        }
        return Optional.of(new JobStatusResponse(
                state.getJobId(),
                state.getStatus(),
                state.getProcessed().get(),
                state.getInserted().get(),
                state.getUpdated().get(),
                state.getSkipped().get(),
                state.getMessage(),
                state.getStartedAt(),
                state.getFinishedAt()
        ));
    }
}
