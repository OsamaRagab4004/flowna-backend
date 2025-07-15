package com.flowna.app.studySquad.StudentAnswer;

    import com.flowna.app.email.EmailService;
    import com.flowna.app.user.User;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import java.util.HashMap;
    import java.util.Map;

@RestController
    @RequestMapping("/api/v1/student-answers")
    @RequiredArgsConstructor
    public class StudentAnswerController {

        private final StudentAnswerService studentAnswerService;
        private final EmailService emailService; // Assuming you have an EmailService for sending emails

        @PostMapping("/save")
        public ResponseEntity<Void> saveStudentAnswers(@RequestBody StudentAnswersPayload payload,
                                                       @AuthenticationPrincipal User user) {
            studentAnswerService.saveStudentAnswers(user.getId(), payload);

            Map<String, Object> variables = new HashMap<>();
            variables.put("msg",user.getUsername() + " answers an exam ");
            emailService.sendSimpleEmail("webflowna@gmail.com",
                    "Completed an exam by " + user.getUsername(),
                    "messageTemplate.html",variables
            );

            return ResponseEntity.ok().build();
        }
    }