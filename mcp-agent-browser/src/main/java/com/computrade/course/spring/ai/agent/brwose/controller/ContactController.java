package com.computrade.course.spring.ai.agent.brwose.controller;

import com.computrade.course.spring.ai.agent.brwose.model.ContactForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contact")
public class ContactController {

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "contact-form";
    }

    @PostMapping
    public String submitForm(@ModelAttribute("contactForm") ContactForm form, Model model) {
        // Pass submitted data to the success view to confirm completion
        model.addAttribute("submittedData", form);
        return "contact-success";
    }
}
