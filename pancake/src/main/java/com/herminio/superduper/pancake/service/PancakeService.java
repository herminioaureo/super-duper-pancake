package com.herminio.superduper.pancake.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azure.communication.email.models.EmailAddress;
import com.herminio.superduper.pancake.model.Contact;
import com.herminio.superduper.pancake.sender.Sender;

@RestController
@RequestMapping("pancake")
public class PancakeService {

    @Autowired
    Sender emailSender;

    @Autowired
    ResourceLoader resourceLoader;

    @PostMapping(path = "sendMessage",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> sendMessage(@RequestBody List<Contact> contact) {
        // Logic to save pancake

        List<EmailAddress> toEmailAddresses = new ArrayList<EmailAddress>();
        EmailAddress emailAddress;

        for (Contact contactElement : contact) {
            emailAddress = new EmailAddress(contactElement.getEmail());
            emailAddress.setDisplayName(contactElement.getName());
            toEmailAddresses.add(emailAddress);
        }

        String content = "";
        try {
            content = new String(
                resourceLoader.getResource("classpath:html/email_template.html")
                    .getContentAsString(StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            e.printStackTrace();
            
        }

        emailSender.send(toEmailAddresses, "🍩 Super Duper Pancake - Welcome!", content.formatted(contact.get(0)));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
