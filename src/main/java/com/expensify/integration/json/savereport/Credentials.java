package com.expensify.integration.json.savereport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Credentials {
    String partnerUserID;
    String partnerUserSecret;
}
