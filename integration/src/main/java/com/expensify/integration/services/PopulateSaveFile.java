package com.expensify.integration.services;

import com.expensify.integration.models.Credentials;
import com.expensify.integration.models.Filters;
import com.expensify.integration.models.InputSettings;
import com.expensify.integration.models.OnReceive;
import com.expensify.integration.models.OutputSettings;
import com.expensify.integration.models.SaveFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class PopulateSaveFile {

    String partnerUserID;

    String partnerUserSecret;

    public PopulateSaveFile(@Value("${expensify.partnerUserID}") String partnerUserID, @Value("${expensify.partnerUserSecret}") String partnerUserSecret) {
        this.partnerUserID = partnerUserID;
        this.partnerUserSecret = partnerUserSecret;
    }

    public PopulateSaveFile() {

    }


    public SaveFile populateSaveFile() {
        Credentials credentials = new Credentials(partnerUserID, partnerUserSecret);

        OnReceive onReceive = new OnReceive(new ArrayList<>(Collections.singleton("returnRandomFileName")));

        InputSettings inputSettings = new InputSettings("combinedReportData",
                "OPEN,SUBMITTED,APPROVED,REIMBURSED,ARCHIVED",
                new Filters("2022-11-01", "2022-12-03", "Expensify Export"));

        OutputSettings outputSettings = new OutputSettings("csv");

        return new SaveFile("file", credentials, onReceive, inputSettings, outputSettings);
    }
}
