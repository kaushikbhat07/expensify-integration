package com.expensify.integration.models.savefile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InputSettings {
    String type;
    String reportState;
    Filters filters;
}
