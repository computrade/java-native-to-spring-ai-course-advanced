package com.computrade.course.spring.ai.claude.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record CompanyNews(
        String headline, // @JsonProperty("headline") - is not needed as it is same
        String source,
        String url,
        String summary,
        long datetime
) implements MarketTimeAware {

    @Override
    @JsonIgnore
    public long getEpochSeconds() {
        return this.datetime;
    }

}