package com.computrade.course.spring.ai.openai.controller;

import com.computrade.course.spring.ai.openai.model.RestfulError;
import org.springframework.ai.tool.execution.ToolExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

// Intercepts the framework failure automatically and responds directly to the browser
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ToolExecutionException.class)
    public ResponseEntity<RestfulError> handleToolExecutionException(ToolExecutionException ex) {
        RestfulError restfulError = RestfulError.builder()
                .exceptionMessage("error with tool name: " + ex.getToolDefinition().name() + ", desc: " + ex.getToolDefinition().description() + ", message: " + ex.getMessage())
                .httpError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(restfulError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestfulError> handleGeneralException(Exception ex) {
        RestfulError restfulError = RestfulError.builder()
                .exceptionMessage("Exception message: " + ex.getMessage())
                .httpError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(Instant.now())
                .build();

        return new ResponseEntity<>(restfulError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
