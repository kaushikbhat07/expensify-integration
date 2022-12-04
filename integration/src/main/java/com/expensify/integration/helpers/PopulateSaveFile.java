package com.expensify.integration.helpers;

import com.expensify.integration.json.savereport.Credentials;
import com.expensify.integration.json.savereport.Filters;
import com.expensify.integration.json.savereport.InputSettings;
import com.expensify.integration.json.savereport.OnReceive;
import com.expensify.integration.json.savereport.OutputSettings;
import com.expensify.integration.json.savereport.SaveReport;

import java.util.ArrayList;
import java.util.Collections;

public class PopulateSaveFile {

    private final String partnerUserID;

    private final String partnerUserSecret;

    public PopulateSaveFile(String partnerUserID, String partnerUserSecret) {
        this.partnerUserID = partnerUserID;
        this.partnerUserSecret = partnerUserSecret;
    }
    public SaveReport populateSaveFile() {
        Credentials credentials = new Credentials(partnerUserID, partnerUserSecret);

        OnReceive onReceive = new OnReceive(new ArrayList<>(Collections.singleton("returnRandomFileName")));

        InputSettings inputSettings = new InputSettings("combinedReportData",
                "OPEN,SUBMITTED,APPROVED,REIMBURSED,ARCHIVED",
                new Filters("2022-11-01", "2022-12-04", "Expensify Export"));

        OutputSettings outputSettings = new OutputSettings("csv");

        return new SaveReport("file", credentials, onReceive, inputSettings, outputSettings);
    }
}
