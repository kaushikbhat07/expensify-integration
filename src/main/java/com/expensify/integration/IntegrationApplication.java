package com.expensify.integration;

import com.expensify.integration.controller.ExpenseController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class IntegrationApplication {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = SpringApplication.run(IntegrationApplication.class, args);
        ExpenseController expenseController = applicationContext.getBean(ExpenseController.class);
        expenseController.retrieveAndSaveExpenses();
    }
}
