package com.expensify.integration.json.savereport;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaveReportJson {
    String type;
    Credentials credentials;
    OnReceive onReceive;
    InputSettings inputSettings;
    OutputSettings outputSettings;
}

