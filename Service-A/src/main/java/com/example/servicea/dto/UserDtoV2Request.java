package com.example.servicea.dto;

import lombok.Data;

@Data
public class UserDtoV2Request {
    private String name;
    private String email;
    private String password;
}
