package com.example.softwareengineer.service;

import com.alibaba.excel.EasyExcel;
import com.example.softwareengineer.repository.CustomerRepository;
import com.example.softwareengineer.upload.CustomerExcelListener;
import com.example.softwareengineer.upload.CustomerUploadRow;
import com.example.softwareengineer.upload.UploadJobState;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerUploadService {

    private static final int BATCH_SIZE = 5000;

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRepository customerRepository;

    public CustomerUploadService(JdbcTemplate jdbcTemplate, CustomerRepository customerRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRepository = customerRepository;
    }

    @Async
    public void processAsync(byte[] bytes, UploadJobState state) {
        try {
            EasyExcel.read(new ByteArrayInputStream(bytes), CustomerUploadRow.class,
                    new CustomerExcelListener(BATCH_SIZE, rows -> processBatch(rows, state)))
                    .sheet()
                    .doRead();
            state.complete("Upload finished");
        } catch (Exception ex) {
            state.fail("Upload failed: " + ex.getMessage());
        }
    }

    public void processBatch(List<CustomerUploadRow> rows, UploadJobState state) {
        Map<String, CustomerUploadRow> byNic = new LinkedHashMap<>();
        long duplicatesInFile = 0;
        for (CustomerUploadRow row : rows) {
            if (row.getNic() == null || row.getNic().isBlank()) {
                state.getSkipped().incrementAndGet();
                continue;
            }
            if (byNic.put(row.getNic().trim(), row) != null) {
                duplicatesInFile++;
            }
        }

        List<String> nics = new ArrayList<>(byNic.keySet());
        Set<String> existingNics = customerRepository.findByNicIn(nics).stream()
                .map(c -> c.getNic().trim())
                .collect(Collectors.toSet());

        List<CustomerUploadRow> inserts = byNic.values().stream()
                .filter(r -> !existingNics.contains(r.getNic().trim()))
                .toList();
        List<CustomerUploadRow> updates = byNic.values().stream()
                .filter(r -> existingNics.contains(r.getNic().trim()))
                .toList();

        if (!inserts.isEmpty()) {
            jdbcTemplate.batchUpdate("INSERT INTO customers (nic, first_name, last_name, email) VALUES (?, ?, ?, ?)", inserts, inserts.size(),
                    (ps, row) -> {
                        ps.setString(1, row.getNic().trim());
                        ps.setString(2, value(row.getFirstName()));
                        ps.setString(3, value(row.getLastName()));
                        ps.setString(4, value(row.getEmail()));
                    });
            state.getInserted().addAndGet(inserts.size());
        }

        if (!updates.isEmpty()) {
            jdbcTemplate.batchUpdate("UPDATE customers SET first_name = ?, last_name = ?, email = ? WHERE nic = ?", updates, updates.size(),
                    (ps, row) -> {
                        ps.setString(1, value(row.getFirstName()));
                        ps.setString(2, value(row.getLastName()));
                        ps.setString(3, value(row.getEmail()));
                        ps.setString(4, row.getNic().trim());
                    });
            state.getUpdated().addAndGet(updates.size());
        }

        state.getProcessed().addAndGet(rows.size());
        state.getSkipped().addAndGet(duplicatesInFile);
    }

    private String value(String input) {
        return Optional.ofNullable(input).map(String::trim).orElse("");
    }
}
