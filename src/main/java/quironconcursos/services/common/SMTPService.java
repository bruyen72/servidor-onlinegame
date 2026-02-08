package quironconcursos.services.common;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
public class SMTPService {

    @Value("${app.url.frontend}")
    private String urlFrontend;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendEmailRegister(String username, String email) {
        Context context = new Context();

        context.setVariable("username", username);

        this.sendEmail(email, "Bem-vindo ao Quiron Concursos", context, "emails/welcome");
    }

    @Async
    public void sendEmailChangePassword(String username, String email) {
        Context context = new Context();

        context.setVariable("username", username);

        this.sendEmail(email, "Senha alterada", context, "emails/change-password");
    }

    @Async
    public void sendEmailResetPassword(String username, String resetPasswordToken, String email) {
        Context context = new Context();

        context.setVariable("username", username);
        context.setVariable("resetPasswordLink", urlFrontend + "/reset-password.html?token=" + resetPasswordToken);

        this.sendEmail(email, "Redefinir senha", context, "emails/reset-password");
    }

    @Async
    public void sendEmailAdmin(List<String> emails, String title, String message) {
        Context context = new Context();

        context.setVariable("title", title);
        context.setVariable("message", message);

        this.sendEmail(emails, title, context, "emails/email-admin");
    }

    private void sendEmail(String email, String subject, Context context, String templateEmail) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setBcc(email);
            mimeMessageHelper.setSubject(subject);

            String htmlContent = templateEngine.process(templateEmail, context);
            mimeMessageHelper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void sendEmail(List<String> emails, String subject, Context context, String templateEmail) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setBcc(emails.toArray(new String[0]));
            mimeMessageHelper.setSubject(subject);

            String htmlContent = templateEngine.process(templateEmail, context);
            mimeMessageHelper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
