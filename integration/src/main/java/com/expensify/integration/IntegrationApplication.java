package com.expensify.integration;

import com.expensify.integration.controller.FetchExpenses;
import com.expensify.integration.exceptions.FileNotFoundException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
@EnableMongoRepositories
public class IntegrationApplication {
    public static void main(String[] args) throws URISyntaxException, IOException, FileNotFoundException {
        ApplicationContext applicationContext = SpringApplication.run(IntegrationApplication.class, args);
        FetchExpenses fetchExpenses = applicationContext.getBean(FetchExpenses.class);
        fetchExpenses.getExpenses();
    }
}
