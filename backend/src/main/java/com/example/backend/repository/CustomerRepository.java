package com.example.backend.repository;

import com.example.backend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @EntityGraph(value = "Customer.detail", type = EntityGraph.EntityGraphType.LOAD)
    Page<Customer> findAll(Pageable pageable);

    @EntityGraph(value = "Customer.detail", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Customer> findById(Long id);

    Optional<Customer> findByNicNumber(String nicNumber);
}
