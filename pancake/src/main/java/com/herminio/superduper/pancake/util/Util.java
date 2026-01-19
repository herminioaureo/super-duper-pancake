package com.herminio.superduper.pancake.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.azure.communication.email.models.EmailAddress;
import com.herminio.superduper.pancake.dto.ContactDTO;
import com.herminio.superduper.pancake.dto.ResponseDTO;
import com.herminio.superduper.pancake.model.Contact;
import com.herminio.superduper.pancake.model.Response;

public class Util {

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public static List<ContactDTO> convertContactToDTO(List<Contact> contacts) {
        return contacts.stream()
            .map(contact -> new ContactDTO(contact.getName(), contact.getEmail()))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<EmailAddress> convertToEmailAddress(List<ContactDTO> contacts) {
        return contacts.stream()
            .map(contact -> {
                EmailAddress emailAddress = new EmailAddress(contact.email());
                emailAddress.setDisplayName(contact.name());
                return emailAddress;
            })
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public static Response convertDtoToResponse(ResponseDTO dto) {
        Response response = new Response();
        response.setErrorCode(dto.getErrorCode());
        response.setStatus(dto.getStatus());
        response.setMessage(dto.getMessage());
        dto.getDetails().forEach(response::addDetail);
        return response;
    }

    
}
