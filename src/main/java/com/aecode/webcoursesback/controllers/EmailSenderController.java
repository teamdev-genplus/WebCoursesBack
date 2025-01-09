package com.aecode.webcoursesback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aecode.webcoursesback.dtos.EmailRequestDTO;
import com.aecode.webcoursesback.services.EmailSenderService;

@RestController
@RequestMapping("/email")
public class EmailSenderController {
    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequestDTO emailRequest) {
        try {
            // Usamos el servicio para enviar el correo
            emailSenderService.sendEmail(emailRequest.getToEmail(), emailRequest.getSubject(), emailRequest.getBody());
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }

}
