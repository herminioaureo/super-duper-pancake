package com.herminio.superduper.pancake.sender;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollerFlux;

@Service
public class AsyncEmailSender implements Sender {

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    @Override
    public void send(List<EmailAddress> to, String subject, String body) {

        final String connectionString = System.getenv("AZURE_EMAIL_CONNECTION_STRING");
        final String senderAddress = "<DoNotReply@780142e5-8660-46de-95ba-ae2be5e22635.azurecomm.net>";

        System.out.println("Sending email to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);

        EmailAsyncClient emailClient = new EmailClientBuilder()
            .connectionString(connectionString)
            .buildAsyncClient();

        EmailMessage message = new EmailMessage()
            .setSenderAddress(senderAddress)
            .setToRecipients(to)
            .setSubject(subject)
            .setBodyHtml(body);

        try {
            // CountDownLatch para aguardar a conclusão da operação
            CountDownLatch latch = new CountDownLatch(1);

            PollerFlux<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(message);

            poller.subscribe(
                response -> {
                    if (response.getStatus() == LongRunningOperationStatus.SUCCESSFULLY_COMPLETED) {
                        System.out.printf("Successfully sent the email (operation id: %s)\n", response.getValue().getId());
                        latch.countDown(); // Libera a thread principal
                    } else if (response.getStatus() == LongRunningOperationStatus.FAILED) {
                        System.out.println("Email send failed, operation id: " + response.getValue().getId());
                        latch.countDown(); // Libera mesmo em caso de falha
                    } else {
                        System.out.println("Email send status: " + response.getStatus() + ", operation id: " + response.getValue().getId());
                    }
                },
                error -> {
                    System.out.println("Error occurred while sending email: " + error.getMessage());
                    latch.countDown(); // Libera em caso de erro
                }
            );

            // Aguarda até a conclusão ou timeout
            boolean completed = latch.await(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            if (!completed) {
                System.out.println("Timeout: Email operation did not complete within " + TIMEOUT.toSeconds() + " seconds");
            }

            System.out.println("Email operation finished.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted: " + e.getMessage());
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }
}