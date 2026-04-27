package com.example.softwareengineer.repository;

import com.example.softwareengineer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @EntityGraph(attributePaths = {"tagLinks", "tagLinks.tag"})
    Optional<Customer> findWithAssociationsById(Long id);

    @EntityGraph(attributePaths = {"tagLinks", "tagLinks.tag"})
    Page<Customer> findAllBy(Pageable pageable);

    boolean existsByNicAndIdNot(String nic, Long id);

    Optional<Customer> findByNic(String nic);

    List<Customer> findByNicIn(Collection<String> nics);
}
