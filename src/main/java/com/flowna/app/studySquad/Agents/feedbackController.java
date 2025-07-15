package com.flowna.app.studySquad.Agents;

import com.flowna.app.email.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/feedback")
public class feedbackController {
    private final EmailService emailService;

    public feedbackController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<String> feedback(@RequestBody Feedback feedback) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("msg","Feedback : " + feedback.getMessage());
        emailService.sendSimpleEmail("webflowna@gmail.com",
                "Feedback from Flowna !!!!!!!!!!!",
                "messageTemplate.html",variables
        );

        return ResponseEntity.ok("Feedback sent successfully");
    }


}

// Placeholder for the Feedback class
class Feedback {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}