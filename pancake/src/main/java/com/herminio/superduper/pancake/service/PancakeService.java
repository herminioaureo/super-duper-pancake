package com.herminio.superduper.pancake.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(path = "sendMessage",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contact> sendMessage(@RequestBody List<Contact> contact) {
        // Logic to save pancake

        List<EmailAddress> toEmailAddresses = contact.stream()
            .map(c -> new EmailAddress(c.getEmail()))
            .toList();

        String content = """
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f5f5f5;
                        padding: 20px;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: #ffffff;
                        border-radius: 10px;
                        padding: 30px;
                        box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                    }
                    h1 {
                        color: #d4a574;
                        text-align: center;
                    }
                    .greeting {
                        font-size: 18px;
                        color: #333;
                        margin-bottom: 20px;
                    }
                    .image-container {
                        text-align: center;
                        margin: 20px 0;
                    }
                    .image-container img {
                        max-width: 100%%;
                        height: auto;
                        border-radius: 8px;
                    }
                    .footer {
                        text-align: center;
                        color: #888;
                        font-size: 12px;
                        margin-top: 30px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>🥞 Super Duper Pancake!</h1>
                    <p class="greeting">Olá, <strong>%s</strong>!</p>
                    <p>Esperamos que você esteja tendo um ótimo dia!</p>
                    
                    <div class="image-container">
                        <img src="https://i.pinimg.com/736x/78/d6/8a/78d68a5864e2b229374dfc2df66ab31b.jpg" 
                             alt="Delicious Pancakes" 
                             width="400"/>
                        <p><em>Imagem de referência: <a href="https://www.pinterest.com/pin/781304235355071164/">Pinterest</a></em></p>
                    </div>
                    
                    <p>Obrigado por fazer parte do nosso estudo!</p>
                    
                    <div class="footer">
                        <p>Este é um email de teste - Super Duper Pancake App</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted("fulano");

        emailSender.send(toEmailAddresses, "🍩 Super Duper Pancake - Welcome!", content);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
