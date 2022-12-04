package com.expensify.integration.controller;

import com.expensify.integration.models.Expense;
import com.expensify.integration.json.downloadreport.DownloadReport;
import com.expensify.integration.csv.Report;
import com.expensify.integration.json.savereport.SaveReport;
import com.expensify.integration.services.ExpenseService;
import com.expensify.integration.helpers.PopulateDownloadReport;
import com.expensify.integration.helpers.PopulateSaveFile;
import com.expensify.integration.helpers.ToJson;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

    public void getExpenses() throws URISyntaxException, IOException {
        // Read template file
        File resource = new ClassPathResource(templatePath).getFile();
        String template = new String(Files.readAllBytes(resource.toPath()));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Populate SaveFile POJO with data
        PopulateSaveFile populateSaveFile = new PopulateSaveFile(partnerUserID, partnerUserSecret);
        SaveReport saveFileObject = populateSaveFile.populateSaveFile();

        // Convert SaveFile POJO to JSON
        ToJson<SaveReport> toJson = new ToJson<>();
        String jsonStr = toJson.toJson(saveFileObject);

        // Create request body
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("requestJobDescription", jsonStr);
        map.add("template", template);

        // POST request to save report
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        final String baseUrl = expensifyBaseUrl;
        URI uri = new URI(baseUrl);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        System.out.println(result);

        String fileName = result.getBody();

        PopulateDownloadReport populateDownloadReport = new PopulateDownloadReport(partnerUserID, partnerUserSecret);
        DownloadReport downloadReportObject = populateDownloadReport.populateDownloadReport(fileName);

        // Convert SaveFile POJO to JSON
        ToJson<DownloadReport> toJsonDownload = new ToJson<>();
        String jsonDownloadStr = toJsonDownload.toJson(downloadReportObject);

        // Create request body
        MultiValueMap<String, String> downloadBody = new LinkedMultiValueMap<>();
        downloadBody.add("requestJobDescription", jsonDownloadStr);

        // POST request to download report
        request = new HttpEntity<>(downloadBody, headers);
        result = restTemplate.postForEntity(uri, request, String.class);
        System.out.println(result);

        String expenseData = result.getBody();

        List<Report> expenseList = new CsvToBeanBuilder(new StringReader(expenseData)).withType(Report.class).build()
                .parse();

        for (Report expense : expenseList) {
            Expense expenses = this.convertExpenseToDatabaseObject(expense);
            expenseService.save(expenses);
        }

        System.out.println("Done!");

        List<Expense> expensesFromDb = expenseService.findAll();
    }

    Expense convertExpenseToDatabaseObject(Report expense) {
        Expense expenses = new Expense();
        expenses.setId((long) (Math.random() + expense.getTransactionId()));
        expenses.setExpenseNumber(expense.getExpenseNumber());
        expenses.setCategory(expense.getCategory());
        expenses.setCurrency(expense.getCurrency());
        expenses.setComment(expense.getComment());
        expenses.setMerchant(expense.getMerchant());
        expenses.setInvoiceId(expense.getInvoiceId());
        expenses.setInvoiceUrl(expense.getInvoiceUrl());
        expenses.setOriginalAmount(expense.getOriginalAmount() / 100);
        expenses.setReportNumber(expense.getReportNumber());
        expenses.setTransactionId(expense.getTransactionId());

        return expenses;
    }
}
