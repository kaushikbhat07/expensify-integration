package com.expensify.integration.models.savefile;

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
