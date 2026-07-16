package com.example.servicea.utility;

import com.example.servicea.entity.UserEntity;

import java.util.UUID;

abstract public class ObjectGenerating {

    protected UserEntity user;

    protected UserEntity generateUser() {
        return user = UserEntity.builder()
                .id(UUID.randomUUID())
                .name("john")
                .email("john@example.com")
                .password("qwerty")
                .build();

    }
}
