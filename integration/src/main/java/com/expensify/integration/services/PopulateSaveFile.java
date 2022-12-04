package com.expensify.integration.services;

import com.expensify.integration.models.savefile.Credentials;
import com.expensify.integration.models.savefile.Filters;
import com.expensify.integration.models.savefile.InputSettings;
import com.expensify.integration.models.savefile.OnReceive;
import com.expensify.integration.models.savefile.OutputSettings;
import com.expensify.integration.models.savefile.SaveFile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class PopulateSaveFile {

    public SaveFile populateSaveFile() {
        Credentials credentials = new Credentials("aa_kaushik_bantval98_gmail_com", "65e87ba15a63cf1a44c314b55b287466ff794086");

        OnReceive onReceive = new OnReceive(new ArrayList<>(Collections.singleton("returnRandomFileName")));

        InputSettings inputSettings = new InputSettings("combinedReportData",
                "OPEN,SUBMITTED,APPROVED,REIMBURSED,ARCHIVED",
                new Filters("2022-11-01", "2022-12-04", "Expensify Export"));

        OutputSettings outputSettings = new OutputSettings("csv");

        return new SaveFile("file", credentials, onReceive, inputSettings, outputSettings);
    }
}
