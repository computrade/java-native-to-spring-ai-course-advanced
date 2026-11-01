package com.computrade.course.spring.ai.openai.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

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

