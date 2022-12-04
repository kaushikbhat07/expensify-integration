package com.expensify.integration.controller;

import com.expensify.integration.exceptions.FileNotFoundException;
import com.expensify.integration.models.Expense;
import com.expensify.integration.json.downloadreport.DownloadReportJson;
import com.expensify.integration.csv.Report;
import com.expensify.integration.json.savereport.SaveReportJson;
import com.expensify.integration.services.ExpenseService;
import com.expensify.integration.helpers.PopulateJson;
import com.expensify.integration.helpers.ToJson;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

@Controller
public class FetchExpenses {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${expensify.baseUrl}")
    private String expensifyBaseUrl;

    @Value("${template.path}")
    private String templatePath;

    @Value("${expensify.partnerUserID}")
    private String partnerUserID;

    @Value("${expensify.partnerUserSecret}")
    private String partnerUserSecret;

    private final ExpenseService expenseService;

    @Autowired
    public FetchExpenses(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public void getExpenses() throws URISyntaxException, IOException, FileNotFoundException {
        // Read template file
        File resource = new ClassPathResource(templatePath).getFile();
        String template = new String(Files.readAllBytes(resource.toPath()));

        // -------------------------------------------------------------------------------------------------------------
        // 1) Generate the report at partner

        // Populate SaveReportJson POJO with data
        PopulateJson populateJson = new PopulateJson(partnerUserID, partnerUserSecret);
        SaveReportJson saveFileObject = populateJson.populateSaveReport();

        // Convert SaveReportJson POJO to JSON
        ToJson<SaveReportJson> convertJavaObjectToJson = new ToJson<>();
        String saveReportJson = convertJavaObjectToJson.toJson(saveFileObject);

        // Send POST request to create expense report
        ResponseEntity<String> response = this.createExpenseReportAtPartner(template, saveReportJson, expensifyBaseUrl);
        String reportFileName = response.getBody();

        // -------------------------------------------------------------------------------------------------------------
        // 2) Download the report from partner

        // Populate DownloadReportJson POJO with data
        PopulateJson populateDownloadReport = new PopulateJson(partnerUserID, partnerUserSecret);
        DownloadReportJson downloadReportJsonObject = populateDownloadReport.populateDownloadReport(reportFileName);

        // Convert DownloadReport POJO to JSON
        ToJson<DownloadReportJson> convertDownloadReportJavaObjectToJson = new ToJson<>();
        String downloadReportJson = convertDownloadReportJavaObjectToJson.toJson(downloadReportJsonObject);

        // Send POST request to download expense report
        response = this.downloadExpenseReportFromPartner(downloadReportJson, expensifyBaseUrl);

        String expenseData = response.getBody();

        // Convert expense report CSV to a Java Object
        List<Report> expenseList = new CsvToBeanBuilder(new StringReader(expenseData)).withType(Report.class).build()
                .parse();

        // -------------------------------------------------------------------------------------------------------------
        // 3) Save expenses to the database

        // Save each expense to the database
        for (Report expense : expenseList) {
            Expense expenses = this.convertExpenseToDatabaseObject(expense);
            expenseService.saveOrUpdate(expenses);
        }
    }

    private ResponseEntity<String> createExpenseReportAtPartner(String template, String json, String baseUrl)
            throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create request body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("requestJobDescription", json);
        requestBody.add("template", template);

        return this.sendPostRequest(headers, requestBody, baseUrl);
    }

    private ResponseEntity<String> downloadExpenseReportFromPartner(String json, String baseUrl)
            throws URISyntaxException, FileNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create request body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("requestJobDescription", json);

        ResponseEntity<String> response =  this.sendPostRequest(headers, requestBody, baseUrl);

        return response;
    }

    private ResponseEntity<String> sendPostRequest(HttpHeaders headers, MultiValueMap<String, String> requestBody,
            String baseUrl) throws URISyntaxException {
        // POST request to save report
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> HttpRequest = new HttpEntity<>(requestBody, headers);
        URI uri = new URI(baseUrl);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, HttpRequest, String.class);

        logger.info(result.getBody());
        return result;
    }

    Expense convertExpenseToDatabaseObject(Report expense) {
        Expense expenses = new Expense();
        expenses.setId(expense.getTransactionId());
        expenses.setExpenseNumber(expense.getExpenseNumber());
        expenses.setCategory(expense.getCategory());
        expenses.setCurrency(expense.getCurrency());
        expenses.setComment(expense.getComment());
        expenses.setMerchant(expense.getMerchant());
        expenses.setInvoiceId(expense.getInvoiceId());
        expenses.setInvoiceUrl(expense.getInvoiceUrl());
        expenses.setAmount(expense.getAmount());
        expenses.setReportNumber(expense.getReportNumber());
        expenses.setTransactionId(expense.getTransactionId());

        return expenses;
    }
}
