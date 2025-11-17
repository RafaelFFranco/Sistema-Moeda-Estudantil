package br.edu.moedaestudantil.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired(required = false)
    private SpringTemplateEngine templateEngine;

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
                logger.info("--- Mensagem simulada para {} ---\n{}\n--- fim mensagem ---", email, msg);
                return;
            }

            if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
                logger.warn("Configuração de email ausente (spring.mail.username/password). Simulando envio para: {}", email);
                logger.info("--- Mensagem simulada para {} ---\n{}\n--- fim mensagem ---", email, msg);
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom(mailUsername);
            message.setSubject("Notificação - Moeda Estudantil");
            message.setText(msg);

            // Log detalhado antes do envio para debugging (não inclui credenciais)
            logger.debug("Enviando email para: {} | from: {} | subject: {}", email, mailUsername, message.getSubject());
            logger.debug("Corpo do email:\n{}", msg);

            mailSender.send(message);
            logger.info("Email enviado com sucesso para: {}", email);
        } catch (Exception e) {
            logger.error("Erro ao enviar email para {}: {}", email, e.getMessage(), e);
            // Não lança exceção para não interromper o fluxo da aplicação
            // Em produção, você pode querer tratar isso de forma diferente
        }
    }

    /**
     * Envia um email HTML renderizado a partir do template Thymeleaf `email/cupom.html`.
     */
    public void sendCupomEmail(String to,
                              String nomeAluno,
                              String nomeVantagem,
                              String nomeEmpresa,
                              String cupom,
                              String cupomValidade,
                              String imagemUrl) {
        try {
            if (!mailEnabled) {
                logger.info("Envio de email desabilitado (app.mail.enabled=false). Simulando envio HTML para: {}", to);
                if (templateEngine != null) {
                    Context ctx = new Context();
                    ctx.setVariable("nomeAluno", nomeAluno);
                    ctx.setVariable("nomeVantagem", nomeVantagem);
                    ctx.setVariable("nomeEmpresa", nomeEmpresa);
                    ctx.setVariable("cupom", cupom);
                    ctx.setVariable("cupomValidade", cupomValidade);
                    String html = templateEngine.process("email/cupom", ctx);
                    logger.info("--- HTML simulado para {} ---\n{}\n--- fim html ---", to, html);
                } else {
                    logger.info("TemplateEngine não disponível; corpo HTML não renderizado.");
                }
                return;
            }

            if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
                logger.warn("Configuração de email ausente (spring.mail.username/password). Simulando envio HTML para: {}", to);
                return;
            }

            String htmlBody = null;
            if (templateEngine != null) {
                Context ctx = new Context();
                ctx.setVariable("nomeAluno", nomeAluno);
                ctx.setVariable("nomeVantagem", nomeVantagem);
                ctx.setVariable("nomeEmpresa", nomeEmpresa);
                ctx.setVariable("cupom", cupom);
                ctx.setVariable("cupomValidade", cupomValidade);
                ctx.setVariable("imagemUrl", imagemUrl);

                htmlBody = templateEngine.process("email/cupom", ctx);
            } else {
                // fallback simples: texto plano contendo cupom
                htmlBody = "<p>Olá " + (nomeAluno == null ? "" : nomeAluno) + ",</p>" +
                        "<p>Seu cupom: <strong>" + cupom + "</strong></p>" +
                        "<p>Válido até: " + (cupomValidade == null ? "" : cupomValidade) + "</p>";
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(to);
            helper.setFrom(mailUsername);
            helper.setSubject("Seu cupom de resgate - Moeda Estudantil");
            helper.setText(htmlBody, true);

            logger.debug("Enviando email HTML para: {} | from: {} | subject: {}", to, mailUsername, "Seu cupom de resgate - Moeda Estudantil");
            mailSender.send(message);
            logger.info("Email HTML enviado com sucesso para: {}", to);
        } catch (Exception e) {
            logger.error("Erro ao enviar email HTML para {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Envia um email HTML para notificar aluno sobre transferência feita por um professor.
     */
    public void sendTransferEmail(String to,
                                  String nomeAluno,
                                  String nomeProfessor,
                                  Integer quantidade,
                                  String descricao,
                                  String dataHora,
                                  Integer saldoAtual) {
        try {
            if (!mailEnabled) {
                logger.info("Envio de email desabilitado (app.mail.enabled=false). Simulando envio HTML para: {}", to);
                if (templateEngine != null) {
                    Context ctx = new Context();
                    ctx.setVariable("nomeAluno", nomeAluno);
                    ctx.setVariable("nomeProfessor", nomeProfessor);
                    ctx.setVariable("quantidade", quantidade);
                    ctx.setVariable("descricao", descricao);
                    ctx.setVariable("dataHora", dataHora);
                    ctx.setVariable("saldoAtual", saldoAtual);
                    String html = templateEngine.process("email/transfer", ctx);
                    logger.info("--- HTML simulado para {} ---\n{}\n--- fim html ---", to, html);
                } else {
                    logger.info("TemplateEngine não disponível; corpo HTML não renderizado.");
                }
                return;
            }

            if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
                logger.warn("Configuração de email ausente (spring.mail.username/password). Simulando envio HTML para: {}", to);
                return;
            }

            String htmlBody = null;
            if (templateEngine != null) {
                Context ctx = new Context();
                ctx.setVariable("nomeAluno", nomeAluno);
                ctx.setVariable("nomeProfessor", nomeProfessor);
                ctx.setVariable("quantidade", quantidade);
                ctx.setVariable("descricao", descricao);
                ctx.setVariable("dataHora", dataHora);
                ctx.setVariable("saldoAtual", saldoAtual);
                htmlBody = templateEngine.process("email/transfer", ctx);
            } else {
                htmlBody = "<p>Olá " + (nomeAluno == null ? "" : nomeAluno) + ",</p>" +
                        "<p>Você recebeu <strong>" + quantidade + "</strong> moedas do professor " + (nomeProfessor == null ? "" : nomeProfessor) + "</p>" +
                        "<p>Mensagem: " + (descricao == null ? "" : descricao) + "</p>" +
                        "<p>Data/hora: " + (dataHora == null ? "" : dataHora) + "</p>" +
                        "<p>Saldo atual: " + (saldoAtual == null ? "" : saldoAtual) + "</p>";
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(to);
            helper.setFrom(mailUsername);
            helper.setSubject("Você recebeu moedas - Moeda Estudantil");
            helper.setText(htmlBody, true);

            logger.debug("Enviando email HTML para: {} | from: {} | subject: {}", to, mailUsername, "Você recebeu moedas - Moeda Estudantil");
            mailSender.send(message);
            logger.info("Email HTML enviado com sucesso para: {}", to);
        } catch (Exception e) {
            logger.error("Erro ao enviar email HTML para {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Envia um email HTML para notificar o professor sobre transferência realizada.
     */
    public void sendTransferEmailToProfessor(String to,
                                             String nomeProfessor,
                                             String nomeAluno,
                                             Integer quantidade,
                                             String descricao,
                                             String dataHora,
                                             Integer saldoAtual) {
        try {
            if (!mailEnabled) {
                logger.info("Envio de email desabilitado (app.mail.enabled=false). Simulando envio HTML para: {}", to);
                if (templateEngine != null) {
                    Context ctx = new Context();
                    ctx.setVariable("nomeProfessor", nomeProfessor);
                    ctx.setVariable("nomeAluno", nomeAluno);
                    ctx.setVariable("quantidade", quantidade);
                    ctx.setVariable("descricao", descricao);
                    ctx.setVariable("dataHora", dataHora);
                    ctx.setVariable("saldoAtual", saldoAtual);
                    String html = templateEngine.process("email/transfer-professor", ctx);
                    logger.info("--- HTML simulado para {} ---\n{}\n--- fim html ---", to, html);
                } else {
                    logger.info("TemplateEngine não disponível; corpo HTML não renderizado.");
                }
                return;
            }

            if (mailUsername == null || mailUsername.isBlank() || mailPassword == null || mailPassword.isBlank()) {
                logger.warn("Configuração de email ausente (spring.mail.username/password). Simulando envio HTML para: {}", to);
                return;
            }

            String htmlBody;
            if (templateEngine != null) {
                Context ctx = new Context();
                ctx.setVariable("nomeProfessor", nomeProfessor);
                ctx.setVariable("nomeAluno", nomeAluno);
                ctx.setVariable("quantidade", quantidade);
                ctx.setVariable("descricao", descricao);
                ctx.setVariable("dataHora", dataHora);
                ctx.setVariable("saldoAtual", saldoAtual);
                htmlBody = templateEngine.process("email/transfer-professor", ctx);
            } else {
                htmlBody = "<p>Olá " + (nomeProfessor == null ? "" : nomeProfessor) + ",</p>" +
                        "<p>Você enviou <strong>" + quantidade + "</strong> moedas para " + (nomeAluno == null ? "" : nomeAluno) + "</p>" +
                        "<p>Mensagem: " + (descricao == null ? "" : descricao) + "</p>" +
                        "<p>Data/hora: " + (dataHora == null ? "" : dataHora) + "</p>" +
                        "<p>Saldo atual: " + (saldoAtual == null ? "" : saldoAtual) + "</p>";
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setTo(to);
            helper.setFrom(mailUsername);
            helper.setSubject("Transferência enviada - Moeda Estudantil");
            helper.setText(htmlBody, true);

            logger.debug("Enviando email HTML para: {} | from: {} | subject: {}", to, mailUsername, "Transferência enviada - Moeda Estudantil");
            mailSender.send(message);
            logger.info("Email HTML enviado com sucesso para: {}", to);
        } catch (Exception e) {
            logger.error("Erro ao enviar email HTML para {}: {}", to, e.getMessage(), e);
        }
    }
}
 