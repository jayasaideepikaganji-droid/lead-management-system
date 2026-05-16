package com.leadmanager.service;

import com.leadmanager.model.Lead;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired(required = false)
    private OpenAIService openAIService;

    @Value("${spring.mail.username:noreply@leadmanager.com}")
    private String fromEmail;

    public void sendAutoReply(Lead lead) {

        if (mailSender == null) {
            System.out.println(
                "Mail sender not configured. Email would be sent to: "
                + lead.getEmail()
            );
            return;
        }

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(lead.getEmail());
            message.setSubject("Thank you for contacting us!");

            String emailBody = generateEmailBody(lead);
            message.setText(emailBody);

            mailSender.send(message);

            System.out.println(
                "Auto-reply sent to: " + lead.getEmail()
            );

        } catch (Exception e) {

            System.err.println(
                "Error sending email: " + e.getMessage()
            );

            throw e;
        }
    }

    private String generateEmailBody(Lead lead) {

        if (openAIService != null) {

            try {

                String aiResponse =
                    openAIService.generatePersonalizedReply(lead);

                if (aiResponse != null && !aiResponse.isEmpty()) {
                    return aiResponse;
                }

            } catch (Exception e) {

                System.err.println(
                    "AI reply generation failed, using template: "
                    + e.getMessage()
                );
            }
        }

        return buildSimpleTemplate(lead);
    }

    private String buildSimpleTemplate(Lead lead) {

        StringBuilder body = new StringBuilder();

        body.append("Dear ")
            .append(lead.getName())
            .append(",\n\n");

        body.append(
            "Thank you for reaching out to us! " +
            "We have received your inquiry regarding "
        );

        body.append(lead.getBusinessType())
            .append(".\n\n");

        body.append("Your message: \"")
            .append(lead.getMessage())
            .append("\"\n\n");

        body.append(
            "Our team will review your request " +
            "and get back to you within 24-48 hours.\n\n"
        );

        body.append(
        	    "If you have any urgent questions, feel free to call us at +91-9876543210.\n\n"
        	);

        body.append("Best regards,\n");
        body.append("Lead Management Team\n");

        return body.toString();
    }
}