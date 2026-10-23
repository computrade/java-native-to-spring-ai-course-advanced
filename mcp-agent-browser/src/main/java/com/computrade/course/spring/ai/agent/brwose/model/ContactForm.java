package com.computrade.course.spring.ai.agent.brwose.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactForm {

    private String fullName;
    private String email;
    private String topic;
    private String message;
    private boolean subscribeNewsletter;

}
