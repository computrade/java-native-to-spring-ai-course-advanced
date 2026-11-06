package com.computrade.course.spring.ai.evaluation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

public record StoreCatalog(
        @JsonProperty(required = true)
        @JsonPropertyDescription("The fitting name of the electronics store")
        String storeName,

        @JsonProperty(required = true)
        @JsonPropertyDescription("The target recommendation category")
        String recommendationCategory,

        @JsonProperty(required = true)
        @JsonPropertyDescription("List of exactly 5 featured products")
        List<ProductInfo> featuredProducts
) {}
