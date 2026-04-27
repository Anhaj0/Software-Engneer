package com.example.softwareengineer.repository;

import com.example.softwareengineer.entity.CustomerTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerTagRepository extends JpaRepository<CustomerTag, Long> {
    Optional<CustomerTag> findByName(String name);
}
