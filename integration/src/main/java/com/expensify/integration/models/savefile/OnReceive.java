package com.expensify.integration.models.savefile;

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
