package com.computrade.course.spring.ai.claude.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxResponse {

    private String ticker;
    private double taxRate;
    private String message;
}
