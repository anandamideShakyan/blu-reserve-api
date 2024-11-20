package com.example.blureserve.apis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {

    @GetMapping(name = "/hello", path = "hello")
    public String helloWorld() {
        return "Hello World";
    }
}
