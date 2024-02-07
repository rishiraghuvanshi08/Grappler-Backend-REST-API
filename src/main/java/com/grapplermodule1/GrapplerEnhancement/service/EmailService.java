package com.grapplermodule1.GrapplerEnhancement.service;

import com.grapplermodule1.GrapplerEnhancement.customexception.PasswordNotMatchException;
import com.grapplermodule1.GrapplerEnhancement.customexception.UserNotFoundException;
import com.grapplermodule1.GrapplerEnhancement.entities.*;
import com.grapplermodule1.GrapplerEnhancement.repository.OtpRepository;
import com.grapplermodule1.GrapplerEnhancement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String sender;


    /**
     * For Generating Password
     *
     * @return String
     */
    public static String generateRandomPassword() {
        String charset = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(charset.length());
            password.append(charset.charAt(randomIndex));
        }

        return password.toString();
    }

    /**
     * For Sending Email
     *
     * @return Boolean
     */
    public Boolean sendSimpleMail(String email) {
        log.info("Send Email Service called");
        try {
            String password = generateRandomPassword();
            Optional<Users> user = userRepository.findByEmail(email);
            user.get().setPassword(passwordEncoder.encode(password));
            userRepository.save(user.get());
            String emailContent = "Hi!\n\nYour password for the Argon-Tracking application has been updated. Below are your new credentials:\n\n"
                    + "Argon-Tracking\n\nJoin using the following credentials:\n\n"
                    + "Email:  " + email + "\nPassword:  " + password;
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(email);
            simpleMailMessage.setText(emailContent);
            simpleMailMessage.setSubject("Reset Password");
            javaMailSender.send(simpleMailMessage);
            log.info("Send Email Service Returning True ");
            return true;
        } catch (Exception e) {
            log.error("Exception In Send Email Service Exception {}", e.getMessage());
            throw e;
        }
    }

}
