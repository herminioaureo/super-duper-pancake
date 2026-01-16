package com.herminio.superduper.pancake.sender;

import java.util.List;

import com.azure.communication.email.models.EmailAddress;

public interface Sender {

    public void send(List<EmailAddress> to, String subject, String body);

}
