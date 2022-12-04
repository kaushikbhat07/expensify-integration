package com.expensify.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.net.URISyntaxException;

@SpringBootApplication
public class IntegrationApplication {
    public static void main(String[] args) throws URISyntaxException {
        SpringApplication.run(IntegrationApplication.class, args);

        GetExpenses getExpenses = new GetExpenses();
        getExpenses.getExpenses();
    }
}
