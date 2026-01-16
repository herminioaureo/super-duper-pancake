package com.herminio.superduper.pancake;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.azure.communication.email.models.EmailAddress;
import com.herminio.superduper.pancake.sender.AsyncEmailSender;
import com.herminio.superduper.pancake.validator.StartupValidator;

@SpringBootApplication
public class PancakeApplication {

	public static void main(String[] args) {
		StartupValidator.validateEnvironmentVariables();
		SpringApplication.run(PancakeApplication.class, args);
		
		/* 
		List<EmailAddress> toEmailAddresses = List.of(
			new EmailAddress("<herminio.aureo@outlook.com>")
		);

		AsyncEmailSender emailSender = new AsyncEmailSender();
		emailSender.send(toEmailAddresses, "Email Azure", "Sucesso.");
		*/
	}

}
