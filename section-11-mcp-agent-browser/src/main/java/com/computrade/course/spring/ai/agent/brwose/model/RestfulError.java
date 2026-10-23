package com.computrade.course.spring.ai.agent.brwose.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.time.Instant;

/**
 * RestfulError
 */

@Data
@Builder
public class RestfulError {

  private HttpStatus httpStatus;

  private String httpError;

  private String exceptionMessage;

  private Instant timestamp;

}

