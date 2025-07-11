package project.self_development.routine.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import project.self_development.routine.domain.EmailVerification;
import project.self_development.routine.repository.EmailRepository;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private final EmailRepository repository;
    private final JavaMailSender javaMailSender;
    private static final String senderEmail="oon418@gmail.com";
    private static final Random random = new Random();
    private static int number;

    public static void createNumber(){
        number = random.nextInt(900000) + 100000; // 100000 ~ 999999
    }
    @Transactional
    public MimeMessage CreateMail(String mail){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try{
            message.setFrom(new InternetAddress(senderEmail, "자기계발 루틴 앱", "UTF-8"));
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("자기계발 루틴 앱 이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body, "UTF-8", "html");

            Optional<EmailVerification> verification = repository.findByEmail(mail);
            if(verification.isPresent()){
                EmailVerification emailVerification = verification.get();
                emailVerification.setAuthNum(number);
                repository.save(emailVerification);
            }
            else{
                EmailVerification entity = new EmailVerification();
                entity.setEmail(mail);
                entity.setAuthNum(number);
                repository.save(entity);
            }
        } catch (MessagingException e){
            if (mail == null || mail.trim().isEmpty()) {
                throw new IllegalArgumentException("이메일 주소가 유효하지 않습니다: " + mail);
            }
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
    public void sendMail(String mail) {
        MimeMessage message = CreateMail(mail);
        javaMailSender.send(message);
    }

    public Optional<EmailVerification> checkMail(String mail){
       return repository.findByEmail(mail);
    }
}
