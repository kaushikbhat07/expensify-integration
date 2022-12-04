package com.expensify.integration.controller;

import com.expensify.integration.csv.Report;
import com.expensify.integration.exceptions.AuthenticationException;
import com.expensify.integration.exceptions.FileNotFoundException;
import com.expensify.integration.helpers.PopulateJson;
import com.expensify.integration.helpers.ToJson;
import com.expensify.integration.json.downloadreport.DownloadReportJson;
import com.expensify.integration.json.savereport.SaveReportJson;
import com.expensify.integration.models.Expense;
import com.expensify.integration.services.ExpenseService;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;

@Controller
@Configuration
@EnableScheduling
public class ExpenseController {
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
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * A scheduled CRON that runs every 10 seconds.
     * 1) Send request to generate the expense report at partner service.
     * 2) Download the expense report from partner service.
     * 3) Persist the expenses to the database.
     */
    @Scheduled(fixedRate = 10000)
    public void retrieveAndSaveExpenses() throws Exception {
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
        List<Report> expenseList = this.convertCsvToJavaObject(expenseData);

        // -------------------------------------------------------------------------------------------------------------
        // 3) Save expenses to the database

        // Save each expense to the database
        this.saveExpenseList(expenseList);
    }

    /**
     * Iterate and save each expense to the Database
     * @param expenseList Expenses array
     */
    private void saveExpenseList(List<Report> expenseList) {
        for (Report expense : expenseList) {
            Expense expenses = this.convertExpenseToDatabaseObject(expense);
            expenseService.saveOrUpdate(expenses);
        }
    }

    /**
     * Converts CSV data returned by partner service to a Java ArrayList.
     * @param expenseData CSV data
     * @return List<Report>
     */
    private List<Report> convertCsvToJavaObject(String expenseData) {
        return new CsvToBeanBuilder(new StringReader(expenseData)).withType(Report.class).build().parse();
    }

    /**
     * Create JSON request body for the POST request that generates the report at the partner service.
     * @param template Template expected by the partner service
     * @param json JSON data
     * @param baseUrl URL
     * @return ResponseEntity
     */
    private ResponseEntity<String> createExpenseReportAtPartner(String template, String json, String baseUrl)
            throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create request body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("requestJobDescription", json);
        requestBody.add("template", template);

        return this.sendPostRequest(headers, requestBody, baseUrl);
    }

    /**
     * Create JSON request body for the POST request that downloads the report from partner service.
     * @param json JSON data
     * @param baseUrl URL
     * @return ResponseEntity
     */
    private ResponseEntity<String> downloadExpenseReportFromPartner(String json, String baseUrl) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create request body
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("requestJobDescription", json);

        return this.sendPostRequest(headers, requestBody, baseUrl);
    }

    /**
     * Sends HTTP POST request using RestTemplate
     * @param headers HTTP headers
     * @param requestBody Request body
     * @param baseUrl URL
     * @return ResponseEntity
     * @throws FileNotFoundException If the report filename provided is incorrect
     * @throws AuthenticationException If the authentication parameters are incorrect
     * @throws Exception General Exception for other HTTP response codes
     */
    private ResponseEntity<String> sendPostRequest(HttpHeaders headers, MultiValueMap<String, String> requestBody,
            String baseUrl) throws Exception {
        // POST request to save report
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> HttpRequest = new HttpEntity<>(requestBody, headers);
        URI uri = new URI(baseUrl);
        ResponseEntity<String> response = restTemplate.postForEntity(uri, HttpRequest, String.class);

        if (response.getStatusCodeValue() == 404) {
            throw new FileNotFoundException("Wrong csv file name provided");
        } else if (response.getStatusCodeValue() == 403) {
            throw new AuthenticationException("Wrong authentication params provided");
        } else if (response.getStatusCodeValue() > 404 && response.getStatusCodeValue() < 500) {
            throw new Exception("Client error/Incorrect request body");
        } else if (response.getStatusCodeValue() >= 500) {
            throw new Exception("Server error");
        } else {
            logger.info(response.getBody());
        }

        return response;
    }

    /**
     * Converts Java Object to a Database Object
     * @param expense Expense java object
     * @return Database object
     */
    private Expense convertExpenseToDatabaseObject(Report expense) {
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
