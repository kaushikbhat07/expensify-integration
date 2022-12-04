package com.expensify.integration.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Report {
    @CsvBindByName(column = "Merchant")
    private String merchant;

    @CsvBindByName(column = "Original Amount")
    private double originalAmount;

    @CsvBindByName(column = "Category")
    String category;

    @CsvBindByName(column = "Comment")
    private String comment;

    @CsvBindByName(column = "Currency")
    private String currency;

    @CsvBindByName(column = "Invoice ID")
    private long invoiceId;

    @CsvBindByName(column = "Invoice URL")
    private String invoiceUrl;

    @CsvBindByName(column = "Transaction ID")
    private long transactionId;

    @CsvBindByName(column = "Report number")
    private long reportNumber;

    @CsvBindByName(column = "Expense number")
    private long expenseNumber;
}
