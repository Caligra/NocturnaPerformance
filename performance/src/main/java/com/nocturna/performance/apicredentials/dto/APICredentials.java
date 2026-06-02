package com.nocturna.performance.apicredentials.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "api_credentials")
@Getter
@Setter
public class APICredentials {
    @Id
    private Integer id = 1; // always 1
    private String clientId;
    private String clientSecret;
    private String tokenValue;
    private Instant expireTime;
}
