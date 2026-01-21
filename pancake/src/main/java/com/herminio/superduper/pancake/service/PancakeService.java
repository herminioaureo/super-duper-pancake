package com.herminio.superduper.pancake.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

import com.herminio.superduper.pancake.dto.ContactDTO;
import com.herminio.superduper.pancake.dto.ResponseDTO;
import com.herminio.superduper.pancake.exception.PancakeException;
import com.herminio.superduper.pancake.model.Contact;
import com.herminio.superduper.pancake.model.Response;
import com.herminio.superduper.pancake.sender.Sender;
import com.herminio.superduper.pancake.util.Util;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    @CircuitBreaker(name = "superDuperPancakeCircuitBreaker", fallbackMethod = "fallback")
    public ResponseEntity<Response> sendMessage(@RequestBody List<Contact> contactList) throws PancakeException {
        log.info("Received sendMessage request with {} contacts.", contactList.size());
        
        ResponseDTO responseDto = new ResponseDTO();    
        List<ContactDTO> contacts = Util.convertContactToDTO(contactList);
     
        // Enviando email para cada contato com sua mensagem específica
        for (ContactDTO contact : contacts) {
            emailSender.send(contact, "🍩 Super Duper Pancake - Welcome!", contact.message(), responseDto);
        }

        Response response = Util.convertDtoToResponse(responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @SuppressWarnings("null")
    private String loadEmailTemplate() throws PancakeException {
        String content = "";
        try {
            content = new String(
                resourceLoader.getResource("classpath:html/email_template.html")
                    .getContentAsString(StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            throw new PancakeException("PANCAKE-002", "Failed to load email template", e);
        }

        return content;
    }

    public ResponseEntity<Response> fallback(Throwable t) {
        log.error("Circuit breaker fallback triggered: " + t.getMessage());

        Response response = new Response();
        response.setStatus("FAILED");
        response.setErrorCode("PANCAKE-ERR-999");
        response.setMessage("Service is currently unavailable. Please try again later.");
        response.addDetail(t.toString());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

}
