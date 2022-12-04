package com.expensify.integration.services;

import com.expensify.integration.models.Expense;
import com.expensify.integration.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense save(Expense expenses) {
        Optional<Expense> expensesOptional = expenseRepository.findById(expenses.getTransactionId());
        return expensesOptional.orElseGet(() -> expenseRepository.save(expenses));
    }

    public Expense saveOrUpdate(Expense expenses) {
        return expenseRepository.save(expenses);
    }

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public Expense findByTransactionId(long transactionId) {
        return expenseRepository.findByTransactionId(transactionId);
    }
}
