package com.herminio.superduper.pancake.sender;

import java.util.List;

import com.herminio.superduper.pancake.dto.ContactDTO;
import com.herminio.superduper.pancake.dto.ResponseDTO;
import com.herminio.superduper.pancake.exception.PancakeException;

public interface Sender {

    public void send(List<ContactDTO> contacts, String subject, String body, ResponseDTO response) throws PancakeException;

}
