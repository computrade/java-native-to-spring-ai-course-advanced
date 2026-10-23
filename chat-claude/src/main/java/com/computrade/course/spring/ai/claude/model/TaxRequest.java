package com.computrade.course.spring.ai.claude.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxRequest {

    private String ticker;
    private String country;
}
