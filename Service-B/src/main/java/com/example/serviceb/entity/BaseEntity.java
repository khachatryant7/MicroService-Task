package com.example.serviceb.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
public class BaseEntity {

    private UUID id;
    @CreationTimestamp
    private Instant createdAt;

}
