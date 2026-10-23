package com.computrade.course.spring.ai.claude.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public interface MarketTimeAware {

    /**
     * Abstract getter to wrap the specific epoch seconds field of the underlying record.
     */
    long getEpochSeconds();

    /**
     * Shared logic to convert the epoch seconds to formatted New York time.
     * Jackson scans interfaces too and will execute this automatically on serialization!
     */
    @JsonProperty("readableTime")
    default String getReadableTime() {
        if (getEpochSeconds() == 0) return "N/A";
        return Instant.ofEpochSecond(getEpochSeconds())
                .atZone(ZoneId.of("America/New_York"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
