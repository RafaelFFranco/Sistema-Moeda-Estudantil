package br.edu.moedaestudantil.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    public void sendEmail(String email, String msg) {
        try {
            if (!mailEnabled) {
                logger.info("Envio de email desabilitado (app.mail.enabled=false). Simulando envio para: {}", email);
                return;
            }

            if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
                logger.warn("Configuração de email ausente (spring.mail.username/password). Simulando envio para: {}", email);
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom(mailUsername);
            message.setSubject("Notificação - Moeda Estudantil");
            message.setText(msg);

            mailSender.send(message);
            logger.info("Email enviado com sucesso para: {}", email);
        } catch (Exception e) {
            logger.error("Erro ao enviar email para {}: {}", email, e.getMessage());
            // Não lança exceção para não interromper o fluxo da aplicação
            // Em produção, você pode querer tratar isso de forma diferente
        }
    }
}
