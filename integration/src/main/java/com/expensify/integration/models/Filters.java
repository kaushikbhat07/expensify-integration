package com.expensify.integration.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Filters {
    String startDate;
    String endDate;
    String markedAsExported;
}
