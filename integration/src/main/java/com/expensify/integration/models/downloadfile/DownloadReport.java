package com.expensify.integration.models.downloadfile;

import com.expensify.integration.models.savefile.Credentials;
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
