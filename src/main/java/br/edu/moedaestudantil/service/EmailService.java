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
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import org.springframework.core.io.ByteArrayResource;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

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

    private byte[] generateQrCodeBytes(String text, int width, int height) throws Exception {
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
            return baos.toByteArray();
        }
    }

    /** Retorna um Data URI (base64) com a imagem PNG do QR code. */
    public String generateQrDataUri(String text, int width, int height) {
        try {
            byte[] bytes = generateQrCodeBytes(text, width, height);
            String base64 = java.util.Base64.getEncoder().encodeToString(bytes);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            logger.warn("Erro ao gerar data URI do QR: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Envia um email HTML renderizado a partir do template Thymeleaf `email/cupom.html`.
     * Se `qrContent` for fornecido, será usado para gerar o QR; caso contrário será gerado um token interno.
     */
    public void sendCupomEmail(String to,
                              String nomeAluno,
                              String nomeVantagem,
                              String nomeEmpresa,
                              String cupom,
                              String cupomValidade,
                              String imagemUrl,
                              String qrContent) {
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

            // Use o conteúdo de QR fornecido, ou gere um token único aqui (cupom|UUID).
            if (qrContent == null || qrContent.isBlank()) {
                qrContent = (cupom == null ? "" : cupom) + "|" + UUID.randomUUID().toString();
            }
            byte[] qrBytes = null;
            try {
                qrBytes = generateQrCodeBytes(qrContent, 300, 300);
            } catch (Exception ex) {
                logger.warn("Falha ao gerar QR code: {}", ex.getMessage());
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setTo(to);
            helper.setFrom(mailUsername);
            helper.setSubject("Seu cupom de resgate - Moeda Estudantil");
            helper.setText(htmlBody, true);

            // Anexa o QR code inline (cid:qrcode) para ser referenciado no template
            try {
                if (qrBytes != null) {
                    helper.addInline("qrcode", new ByteArrayResource(qrBytes), "image/png");
                }
            } catch (Exception ex) {
                logger.warn("Não foi possível anexar QR code ao email: {}", ex.getMessage());
            }

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
 