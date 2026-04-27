package com.example.backend.service;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.backend.dto.CustomerExcelModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerUploadListener extends AnalysisEventListener<CustomerExcelModel> {

    private static final int BATCH_COUNT = 5000;
    private List<CustomerExcelModel> cachedDataList = new ArrayList<>(BATCH_COUNT);
    private final JdbcTemplate jdbcTemplate;
    private final String jobId;
    private AtomicInteger processedCount;

    public CustomerUploadListener(JdbcTemplate jdbcTemplate, String jobId, AtomicInteger processedCount) {
        this.jdbcTemplate = jdbcTemplate;
        this.jobId = jobId;
        this.processedCount = processedCount;
    }

    @Override
    public void invoke(CustomerExcelModel data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!cachedDataList.isEmpty()) {
            saveData();
        }
    }

    private void saveData() {
        if (cachedDataList.isEmpty()) return;

        // Using ON DUPLICATE KEY UPDATE or IGNORE based on NIC
        String customerSql = "INSERT INTO customer (name, nic_number, dob) VALUES (?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE name=VALUES(name), dob=VALUES(dob)";
                             
        jdbcTemplate.batchUpdate(customerSql, cachedDataList, BATCH_COUNT,
            (ps, item) -> {
                ps.setString(1, item.getName());
                ps.setString(2, item.getNicNumber());
                if (item.getDob() != null && !item.getDob().trim().isEmpty()) {
                    try {
                        ps.setDate(3, java.sql.Date.valueOf(LocalDate.parse(item.getDob(), DateTimeFormatter.ISO_DATE)));
                    } catch (Exception e) {
                        ps.setNull(3, java.sql.Types.DATE);
                    }
                } else {
                    ps.setNull(3, java.sql.Types.DATE);
                }
            });

        // Insert mobiles (Requires knowing customer_id, so we need a secondary step to fetch IDs or use insert select)
        String mobileSql = "INSERT IGNORE INTO customer_mobile (customer_id, mobile_number) " +
                           "SELECT id, ? FROM customer WHERE nic_number = ?";
        jdbcTemplate.batchUpdate(mobileSql, cachedDataList, BATCH_COUNT,
            (ps, item) -> {
                ps.setString(1, item.getMobileNumber());
                ps.setString(2, item.getNicNumber());
            });

        // Insert addresses conditionally if city and country are provided
        String addressSql = "INSERT INTO customer_address (customer_id, city_id, country_id) " +
                            "SELECT id, ?, ? FROM customer WHERE nic_number = ?";
        jdbcTemplate.batchUpdate(addressSql, cachedDataList, BATCH_COUNT,
            (ps, item) -> {
                if (item.getCityId() != null) { ps.setLong(1, item.getCityId()); } else { ps.setNull(1, java.sql.Types.BIGINT); }
                if (item.getCountryId() != null) { ps.setLong(2, item.getCountryId()); } else { ps.setNull(2, java.sql.Types.BIGINT); }
                ps.setString(3, item.getNicNumber());
            });

        processedCount.addAndGet(cachedDataList.size());
    }
}
