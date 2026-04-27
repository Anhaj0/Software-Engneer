package com.example.softwareengineer.service;

import com.example.softwareengineer.dto.CustomerRequest;
import com.example.softwareengineer.dto.CustomerResponse;
import com.example.softwareengineer.entity.Customer;
import com.example.softwareengineer.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.findByNic(request.nic()).isPresent()) {
            throw new DataIntegrityViolationException("NIC already exists: " + request.nic());
        }
        Customer customer = toEntity(new Customer(), request);
        return toResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        if (customerRepository.existsByNicAndIdNot(request.nic(), id)) {
            throw new DataIntegrityViolationException("NIC already exists: " + request.nic());
        }
        return toResponse(customerRepository.save(toEntity(customer, request)));
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findWithAssociationsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + id));
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> list(Pageable pageable) {
        return customerRepository.findAllBy(pageable).map(this::toResponse);
    }

    private Customer toEntity(Customer customer, CustomerRequest request) {
        customer.setNic(request.nic());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());
        return customer;
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getNic(), customer.getFirstName(), customer.getLastName(), customer.getEmail());
    }
}
