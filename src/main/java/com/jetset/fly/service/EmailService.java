package com.jetset.fly.service;

import jakarta.mail.MessagingException;
import org.thymeleaf.context.Context;



import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import com.itextpdf.text.pdf.BaseFont;
import com.jetset.fly.model.Booking;
import com.jetset.fly.model.Passenger;
import com.jetset.fly.service.EmailService;
@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	 
    private final JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;
  

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendBookingConfirmationEmail(Booking booking, List<Passenger> passengers) {
        try {
            Context context = new Context();
            context.setVariable("booking", booking);
            context.setVariable("passengers", passengers);

            String htmlContent = templateEngine.process("email/confirmation", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("Booking Confirmed - JetSetFly");
            helper.setText(htmlContent, true); // true = isHtml

            javaMailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send confirmation email.");
        }
    }

    public void sendBookingConfirmationWithAttachment(String toEmail, File pdfFile) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Your Booking Confirmation");
        helper.setText("Please find attached your booking confirmation.");

        helper.addAttachment(pdfFile.getName(), pdfFile);

        javaMailSender.send(message);
    }
    
    
    
    ////////////////////
    public void sendOtpEmail(String to, String otp) {
        try {
            Context context = new Context();
            context.setVariable("otp", otp);

            String htmlContent = templateEngine.process("email/resetPassOTP", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Password Reset OTP - JetSetFly");
            helper.setText(htmlContent, true); // true = HTML

            // Load image from resources
            ClassPathResource logo = new ClassPathResource("static/assets/gg.png.png");

            // Embed image inline using the same CID as in the template
            helper.addInline("logoImage", logo);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send OTP email.");
        }
    }
    
    public void sendReviewEmail(String to) {
        try {
            Context context = new Context();

            String htmlContent = templateEngine.process("email/reviewEmail", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Review JetSetFly");
            helper.setText(htmlContent, true); // true = HTML

            // Load image from resources
            ClassPathResource logo = new ClassPathResource("static/assets/gg.png.png");
            ClassPathResource videogfi = new ClassPathResource("static/assets/video.gif");
            ClassPathResource happy1 = new ClassPathResource("static/assets/happy1.png");
            ClassPathResource person2 = new ClassPathResource("static/assets/person2.png");
            ClassPathResource person3 = new ClassPathResource("static/assets/person3.png");

            ClassPathResource side = new ClassPathResource("static/assets/side.png");
            ClassPathResource side1 = new ClassPathResource("static/assets/side1.png");
            ClassPathResource side2 = new ClassPathResource("static/assets/side2.png");
            
            // Embed image inline using the same CID as in the template
            helper.addInline("logo", logo);
            helper.addInline("videogfi", videogfi);
            helper.addInline("happy1", happy1);
            helper.addInline("person2", person2);
            helper.addInline("person3", person3);
            helper.addInline("side", side);
            helper.addInline("side1", side1);
            helper.addInline("side2", side2);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email.");
        }
    }


   
    
   
   

}
