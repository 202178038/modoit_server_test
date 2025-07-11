package project.self_development.routine.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import project.self_development.routine.domain.EmailVerification;
import project.self_development.routine.dto.EmailDto;
import project.self_development.routine.dto.EmailRequestDto;
import project.self_development.routine.service.MailService;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class EmailController {
    private final MailService mailService;

    @PostMapping("/email-auth")
    public ResponseEntity<?> mailSend(@Valid @RequestBody EmailRequestDto emailDto) throws MessagingException {
        mailService.sendMail(emailDto.getEmail());
        return ResponseEntity.ok("인증 코드가 발송되었습니다.");
    }

    @PostMapping("/email-check")
    public ResponseEntity<?> verify(@Valid @RequestBody EmailDto emailDto) {
        Optional<EmailVerification> emailVerification = mailService.checkMail(emailDto.getEmail());

        if (emailVerification.isPresent() &&
                emailVerification.get().getAuthNum() == emailDto.getAuthNum()) {
            return ResponseEntity.ok("인증이 완료되었습니다.");
        }
        return ResponseEntity.status(400).body("인증에 실패하였습니다.");
    }
}
