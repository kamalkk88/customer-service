package com.hotel.customerservice.entities;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerCredentialRepository extends JpaRepository<CustomerCredential, String> {
}