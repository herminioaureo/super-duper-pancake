package com.herminio.superduper.pancake.dto;

import java.util.List;

import com.herminio.superduper.pancake.model.Contact;

public record SendMessageRequestDTO(List<Contact> contacts) {

}
