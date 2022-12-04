package com.expensify.integration.helpers;

import com.expensify.integration.json.downloadreport.DownloadReportJson;
import com.expensify.integration.json.savereport.Credentials;
import com.expensify.integration.json.savereport.Filters;
import com.expensify.integration.json.savereport.InputSettings;
import com.expensify.integration.json.savereport.OnReceive;
import com.expensify.integration.json.savereport.OutputSettings;
import com.expensify.integration.json.savereport.SaveReportJson;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Helper class to populate Java Objects to be used for HTTP requests.
 */
public class PopulateJson {

    private static String partnerUserID;

    private static String partnerUserSecret;

    public PopulateJson(String partnerUser, String partnerSecret) {
        partnerUserID = partnerUser;
        partnerUserSecret = partnerSecret;
    }

    public SaveReportJson populateSaveReport() {
        Credentials credentials = new Credentials(partnerUserID, partnerUserSecret);

        OnReceive onReceive = new OnReceive(new ArrayList<>(Collections.singleton("returnRandomFileName")));

        // Get today's date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();

        // Get start date
        LocalDateTime startDate = LocalDateTime.of(2022, Month.NOVEMBER, 1, 0, 0);

        InputSettings inputSettings = new InputSettings("combinedReportData",
                "OPEN,SUBMITTED,APPROVED,REIMBURSED,ARCHIVED",
                new Filters(dtf.format(startDate), dtf.format(now), "Expensify Export"));

        OutputSettings outputSettings = new OutputSettings("csv");

        return new SaveReportJson("file", credentials, onReceive, inputSettings, outputSettings);
    }

    public DownloadReportJson populateDownloadReport(String fileName) {
        Credentials credentials = new Credentials(partnerUserID, partnerUserSecret);
        String type = "download";
        String fileSystem = "integrationServer";
        return new DownloadReportJson(type, credentials, fileName, fileSystem);
    }
}
