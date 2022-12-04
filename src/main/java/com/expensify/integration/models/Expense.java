package com.expensify.integration.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    long id;

    String merchant;

    double amount;

    String category;

    String comment;

    String currency;

    long invoiceId;

    String invoiceUrl;

    long transactionId;

    long reportNumber;

    long expenseNumber;
}
