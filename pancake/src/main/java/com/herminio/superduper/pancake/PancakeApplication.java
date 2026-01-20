package com.herminio.superduper.pancake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.herminio.superduper.pancake.validator.StartupValidator;

@SpringBootApplication
public class PancakeApplication {

	public static void main(String[] args) {
		StartupValidator.validateEnvironmentVariables();
		SpringApplication.run(PancakeApplication.class, args);
	}

}
