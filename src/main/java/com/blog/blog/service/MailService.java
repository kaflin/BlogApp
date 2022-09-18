package com.blog.blog.service;
import com.blog.blog.Model.NotificationEmail;
import com.blog.blog.exceptions.SpringRedditException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Component
public class MailService {

    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;

    public MailService(JavaMailSender mailSender, MailContentBuilder mailContentBuilder) {
        this.mailSender = mailSender;
        this.mailContentBuilder = mailContentBuilder;
    }

    @Async
     void sendMail(NotificationEmail notificationEmail) {
         MimeMessagePreparator messagePreparator= mimeMessage -> {
             MimeMessageHelper messageHelper=new MimeMessageHelper(mimeMessage);
             messageHelper.setFrom("blog@gmail.com");
             messageHelper.setTo(notificationEmail.getRecipient());
             messageHelper.setSubject(notificationEmail.getSubject());
             messageHelper.setText(notificationEmail.getBody());

         };
         try
         {
             mailSender.send(messagePreparator);
             log.info("Activation email Sent!!");
         }
         catch(MailException e){
             log.error("Exception Occurred when sending mail",e);
             throw new SpringRedditException("Exception Occurred when sending mail to"+" "+notificationEmail.getRecipient(),e);


         }
    }
}
