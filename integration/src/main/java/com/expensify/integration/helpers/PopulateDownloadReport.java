package com.expensify.integration.helpers;

import com.expensify.integration.json.downloadreport.DownloadReport;
import com.expensify.integration.json.savereport.Credentials;

public class PopulateDownloadReport {
    private final String partnerUserID;

    private final String partnerUserSecret;

    public PopulateDownloadReport(String partnerUserID, String partnerUserSecret) {
        this.partnerUserID = partnerUserID;
        this.partnerUserSecret = partnerUserSecret;
    }

    public DownloadReport populateDownloadReport(String fileName) {
        Credentials credentials = new Credentials(partnerUserID, partnerUserSecret);
        String type = "download";
        String fileSystem = "integrationServer";
        return new DownloadReport(type, credentials, fileName, fileSystem);
    }
}
