package com.expensify.integration.json.savereport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OnReceive {
    List<String> immediateResponse;
}
