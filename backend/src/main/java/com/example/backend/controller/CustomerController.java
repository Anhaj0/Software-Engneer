package com.example.backend.controller;

import com.example.backend.dto.CustomerDto;
import com.example.backend.service.AsyncCustomerUploadService;
import com.example.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*") // For development purposes
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AsyncCustomerUploadService uploadService;

    @GetMapping
    public Page<CustomerDto> getAllCustomers(Pageable pageable) {
        return customerService.getAllCustomers(pageable);
    }

    @GetMapping("/{id}")
    public CustomerDto getCustomer(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto createCustomer(@RequestBody CustomerDto dto) {
        return customerService.createCustomer(dto);
    }

    @PutMapping("/{id}")
    public CustomerDto updateCustomer(@PathVariable Long id, @RequestBody CustomerDto dto) {
        return customerService.updateCustomer(id, dto);
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<Map<String, String>> uploadCustomers(@RequestParam("file") MultipartFile file) {
        try {
            String jobId = UUID.randomUUID().toString();
            uploadService.processExcelUpload(file.getInputStream(), jobId);
            
            Map<String, String> response = new HashMap<>();
            response.put("jobId", jobId);
            response.put("status", "QUEUED");
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
