package com.expensify.integration.models.report;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Report {
    @CsvBindByName(column = "Merchant")
    private String merchant;

    @CsvBindByName(column = "Original Amount")
    private String originalAmount;

    @CsvBindByName(column = "Category")
    String category;

    @CsvBindByName(column = "Comment")
    private String comment;

    @CsvBindByName(column = "Currency")
    private String currency;

    @CsvBindByName(column = "Invoice ID")
    private String invoiceId;

    @CsvBindByName(column = "Invoice URL")
    private String invoiceUrl;

    @CsvBindByName(column = "Transaction ID")
    private String transactionId;

    @CsvBindByName(column = "Report number")
    private String reportNumber;

    @CsvBindByName(column = "Expense number")
    private String expenseNumber;
}
