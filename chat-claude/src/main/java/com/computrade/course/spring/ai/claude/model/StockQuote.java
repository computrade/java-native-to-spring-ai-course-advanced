package com.computrade.course.spring.ai.claude.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record StockQuote(
        String symbol,
        @JsonProperty("c") double currentPrice,
        @JsonProperty("h") double highPrice,
        @JsonProperty("l") double lowPrice,
        @JsonProperty("o") double openPrice,
        @JsonProperty("d") double changeInPrice,
        @JsonProperty("dp") double percentPriceChange,
        @JsonProperty("pc") double previousClosePrice,
        @JsonProperty("t") long timestamp
) implements MarketTimeAware {

    @Override
    @JsonIgnore
    public long getEpochSeconds() {
        return this.timestamp;
    }

    // A fluent "wither" method to attach the ticker seamlessly
    public StockQuote withTicker(String tickerSymbol) {
        return new StockQuote(tickerSymbol, this.currentPrice, this.highPrice, this.lowPrice,
                this.openPrice, this.changeInPrice, this.percentPriceChange, this.previousClosePrice, this.timestamp);
    }

}

