package com.expensify.integration.json.downloadreport;

import com.expensify.integration.json.savereport.Credentials;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DownloadReport {
    String type;
    Credentials credentials;
    String fileName;
    String fileSystem;
}
