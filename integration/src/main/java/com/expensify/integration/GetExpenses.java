package com.expensify.integration;

import com.expensify.integration.models.downloadfile.DownloadReport;
import com.expensify.integration.models.report.Report;
import com.expensify.integration.models.savefile.SaveFile;
import com.expensify.integration.services.PopulateDownloadReport;
import com.expensify.integration.services.PopulateSaveFile;
import com.expensify.integration.services.ToJson;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class GetExpenses {
    void getExpenses() throws URISyntaxException {
        String template = "<#if addHeader == true>\n" + "    Merchant,Original Amount,Category,Comment,Currency,Invoice ID,Invoice URL,Transaction ID,Report number,Expense number<#lt>\n" + "</#if>\n" + "<#assign reportNumber = 1>\n" + "<#assign expenseNumber = 1>\n" + "<#list reports as report>\n" + "    <#list report.transactionList as expense>\n" + "        ${expense.merchant},<#t>\n" + "        <#-- note: expense.amount prints the original amount only -->\n" + "        ${expense.amount},<#t>\n" + "        ${expense.category},<#t>\n" + "        ${expense.comment},<#t>\n" + "        ${expense.currency},<#t>\n" + "        ${expense.receiptID},<#t>\n" + "        ${expense.receiptObject.url },<#t>\n" + "        ${expense.transactionID},<#t>\n" + "        ${reportNumber},<#t>\n" + "        ${expenseNumber}<#lt>\n" + "        <#assign expenseNumber = expenseNumber + 1>\n" + "    </#list>\n" + "    <#assign reportNumber = reportNumber + 1>\n" + "</#list>";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Populate SaveFile POJO with data
        PopulateSaveFile populateSaveFile = new PopulateSaveFile();
        SaveFile saveFileObject = populateSaveFile.populateSaveFile();

        // Convert SaveFile POJO to JSON
        ToJson<SaveFile> toJson = new ToJson<>();
        String jsonStr = toJson.toJson(saveFileObject);

        // Create request body
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("requestJobDescription", jsonStr);
        map.add("template", template);

        // POST request to save report
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        final String baseUrl = "https://integrations.expensify.com/Integration-Server/ExpensifyIntegrations";
        URI uri = new URI(baseUrl);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        System.out.println(result);

        String fileName = result.getBody();

        PopulateDownloadReport populateDownloadReport = new PopulateDownloadReport();
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

        List<Report> expenseList = new CsvToBeanBuilder(new StringReader(expenseData))
                .withType(Report.class)
                .build()
                .parse();
    }
}
