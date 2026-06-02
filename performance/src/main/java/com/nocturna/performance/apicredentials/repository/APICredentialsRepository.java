package com.nocturna.performance.apicredentials.repository;

import com.nocturna.performance.apicredentials.dto.APICredentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface APICredentialsRepository extends JpaRepository<APICredentials, Integer> {
}
