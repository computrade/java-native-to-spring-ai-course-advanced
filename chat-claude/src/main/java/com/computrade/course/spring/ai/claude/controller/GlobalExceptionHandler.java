package com.computrade.course.spring.ai.claude.controller;

import com.computrade.course.spring.ai.claude.model.RestfulError;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ToolExecutionException.class)
    public ResponseEntity<RestfulError> handleToolExecutionException(ToolExecutionException ex) {
        // Intercepts the framework failure automatically and responds directly to the browser
        RestfulError restfulError = RestfulError.builder()
                .exceptionMessage(ex.getMessage())
                .httpError(HttpStatus.FORBIDDEN.getReasonPhrase())
                .httpStatus(HttpStatus.FORBIDDEN)
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(restfulError, HttpStatus.FORBIDDEN);
    }

}
