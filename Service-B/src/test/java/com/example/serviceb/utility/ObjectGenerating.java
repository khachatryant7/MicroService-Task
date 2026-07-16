package com.example.serviceb.utility;

import com.example.serviceb.entity.UserEntity;

import java.util.UUID;

public class ObjectGenerating {

    protected UserEntity user;

    protected UserEntity generateUser(){
        return UserEntity.builder()
                .id(UUID.randomUUID())
                .name("Sergey")
                .email("Sergey@example.com")
                .password("qwerty")
                .build();
    }

}
