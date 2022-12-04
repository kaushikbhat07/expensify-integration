package com.expensify.integration.services;

import com.expensify.integration.models.downloadfile.DownloadReport;
import com.expensify.integration.models.savefile.Credentials;
import org.springframework.stereotype.Service;

@Service
public class PopulateDownloadReport {
    public DownloadReport populateDownloadReport(String fileName) {
        Credentials credentials = new Credentials("aa_kaushik_bantval98_gmail_com", "65e87ba15a63cf1a44c314b55b287466ff794086");
        String type = "download";
        String fileSystem = "integrationServer";
        return new DownloadReport(type, credentials, fileName, fileSystem);
    }
}
