package com.computrade.course.spring.ai.evaluation.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ProductInfo(
        @JsonPropertyDescription("Product manufacturer or brand name")
        String manufacturer,

        @JsonPropertyDescription("Exact product model name and specifications")
        String productName,

        @JsonPropertyDescription("Retail price in USD without currency symbols")
        double priceUSD,

        @JsonPropertyDescription("Current inventory status")
        ProductStatus status
) {}

enum ProductStatus {
    AVAILABLE,
    PRE_ORDER,
    OUT_OF_STOCK
}