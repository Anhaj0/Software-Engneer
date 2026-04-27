package com.example.softwareengineer.dto;

public record CustomerResponse(
        Long id,
        String nic,
        String firstName,
        String lastName,
        String email
) {
}
