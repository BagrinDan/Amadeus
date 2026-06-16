package com.amadeus.backend;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @GetMapping("/get_env")
    public String env() {
        System.out.println(dbUrl);
        return dbUrl;
    }
}
