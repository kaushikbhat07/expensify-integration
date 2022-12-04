package com.expensify.integration.models.savefile;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SaveFile {
    String type;
    Credentials credentials;
    OnReceive onReceive;
    InputSettings inputSettings;
    OutputSettings outputSettings;
}

