package com.herminio.superduper.pancake.sender;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Override
    public void send(List<EmailAddress> to, String subject, String body) {

        final String connectionString = System.getenv("AZURE_EMAIL_CONNECTION_STRING");
        final String senderAddress = "<DoNotReply@780142e5-8660-46de-95ba-ae2be5e22635.azurecomm.net>";

         // Implementation for sending email
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
            //.setBodyPlainText(body);
            .setBodyHtml(body);

        try {
            
            Duration MAIN_THREAD_WAIT_TIME = Duration.ofSeconds(30);

            // ExecutorService to run the polling in a separate thread
            ExecutorService executorService = Executors.newSingleThreadExecutor();

            PollerFlux<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(message);

            executorService.submit(() -> {
                // The initial request is sent out as soon as we subscribe the to PollerFlux object
                poller.subscribe(
                    response -> {
                        if (response.getStatus() == LongRunningOperationStatus.SUCCESSFULLY_COMPLETED) {
                            System.out.printf("Successfully sent the email (operation id: %s)\n", response.getValue().getId());
                        }
                        else {
                            // The operation ID can be retrieved as soon as the first response is received from the PollerFlux.
                            System.out.println("Email send status: " + response.getStatus() + ", operation id: " + response.getValue().getId());
                        }
                    },
                    error -> {
                        System.out.println("Error occurred while sending email: " + error.getMessage());
                    }
                );
            });

        // In a real application, you might have a mechanism to keep the main thread alive.
        // For this sample we will keep the main thread alive for 30 seconds to make sure the child thread has time to receive the SUCCESSFULLY_COMPLETED status.
        try {
            Thread.sleep(MAIN_THREAD_WAIT_TIME.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        System.out.println("Main thread ends.");

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    
    }

}
