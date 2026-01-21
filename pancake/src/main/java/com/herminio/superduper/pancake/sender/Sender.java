package com.herminio.superduper.pancake.sender;

import com.herminio.superduper.pancake.dto.ContactDTO;
import com.herminio.superduper.pancake.dto.ResponseDTO;
import com.herminio.superduper.pancake.exception.PancakeException;

public interface Sender {

    public void send(ContactDTO contact, String subject, String body, ResponseDTO response) throws PancakeException;

}
