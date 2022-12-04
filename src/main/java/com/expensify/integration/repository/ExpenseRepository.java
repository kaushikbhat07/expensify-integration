package com.expensify.integration.repository;

import com.expensify.integration.models.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExpenseRepository extends MongoRepository<Expense, Long> {
    Expense findByTransactionId(long transactionId);
}
