package webdoc.authentication.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import webdoc.authentication.repository.UserRepository;

/*
 * 이메일 작성 서비스
 */
@Service
@RequiredArgsConstructor
@EnableAsync
public class EmailService {
    private final JavaMailSender emailSender;


    private final SpringTemplateEngine templateEngine;


    //메일 양식 작성 로직
    public MimeMessage createEmailForm(String email,String code,String title) throws MessagingException{

        String setFrom = "springbank0625@gmail.com"; //email-config에 설정한 자신의 이메일 주소(보내는 사람)
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email); //보낼 이메일 설정
        message.setSubject(title); //제목 설정
        message.setFrom(setFrom); //보내는 이메일
        message.setText(setContext(code,email), "utf-8", "html");
        return message;
    }


    //메일 전송 로직
    @Async
    public void sendEmail(String toEmail,String code,String title) throws MessagingException {

        //메일전송에 필요한 정보 설정
        MimeMessage emailForm = createEmailForm(toEmail,code,title);
        //실제 메일 전송
        emailSender.send(emailForm);

        return;
    }

    //타임리프를 이용한 context 설정
    public String setContext(String code,String email) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("email",email);
        return templateEngine.process("mail", context); //mail.html
    }

}