package com.example.serviceb.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceBController {

    @GetMapping("/hello")
    public String ServiceB(){
        return "Hello from service B!";
    }
}
