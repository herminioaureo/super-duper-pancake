package com.herminio.superduper.pancake.sender;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailAddress;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollerFlux;
import com.herminio.superduper.pancake.dto.ContactDTO;
import com.herminio.superduper.pancake.dto.ResponseDTO;
import com.herminio.superduper.pancake.exception.PancakeException;
import com.herminio.superduper.pancake.util.Util;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsyncEmailSender implements Sender {

    @Value("${pancake.email.timeout-seconds:15}")
    private long timeoutSeconds;

    @Value("${pancake.email.force-error:false}")
    private boolean forceError;

    @Override
    public void send(List<ContactDTO> contacts, String subject, String body, ResponseDTO responseDto) throws PancakeException {

        // Forçar erro para teste do circuit breaker
        if (forceError) {
            log.error("Forced error enabled - throwing exception for circuit breaker test");
            throw new PancakeException("PANCAKE-TEST-001", "Forced error for circuit breaker testing");
        }

        log.info("Starting process to send email...");
        List<EmailAddress> to = Util.convertToEmailAddress(contacts);

        final String connectionString = System.getenv("AZURE_EMAIL_CONNECTION_STRING");
        final String senderAddress = "<DoNotReply@780142e5-8660-46de-95ba-ae2be5e22635.azurecomm.net>";

        EmailAsyncClient emailClient = new EmailClientBuilder()
            .connectionString(connectionString)
            .buildAsyncClient();

        for (int i = 0; i < to.size(); i++) {
            EmailAddress emailAddress = to.get(i);

            if (!Util.isValidEmail(emailAddress.getAddress())) {
                log.info("Invalid recipient email address: " + emailAddress.getAddress());
                responseDto.setStatus("PARTIALLY SUCCESS");
                responseDto.setMessage("Message sent with some invalid email addresses");
                responseDto.addDetail("Invalid recipient email address: " + emailAddress.getAddress());
                to.remove(i);
                i--;
            }
        }

        EmailMessage message = new EmailMessage()
            .setSenderAddress(senderAddress)
            .setToRecipients(to)
            .setSubject(subject)
            .setBodyHtml(body);

        try {
            // CountDownLatch para aguardar a conclusão da operação
            CountDownLatch latch = new CountDownLatch(1);
            final PancakeException[] asyncException = new PancakeException[1]; // Para capturar exceção do callback
            PollerFlux<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(message);

            poller.subscribe(
                response -> {
                    if (response.getStatus() == LongRunningOperationStatus.SUCCESSFULLY_COMPLETED) {
                        log.info("Successfully sent the email (operation id: %s)\n", response.getValue().getId());
                        latch.countDown(); // Libera a thread principal
                    } else if (response.getStatus() == LongRunningOperationStatus.FAILED) {
                        log.error("Email send failed, operation id: " + response.getValue().getId());
                        asyncException[0] = new PancakeException("PANCAKE-ERR-001", "Failed to send email, operation id: " + response.getValue().getId());
                        latch.countDown(); // Libera mesmo em caso de falha
                    } else {
                        log.info("Email send status: " + response.getStatus() + ", operation id: " + response.getValue().getId());
                    }
                },
                error -> {
                    log.error("Error occurred while sending email: " + error.getMessage());
                    asyncException[0] = new PancakeException("PANCAKE-ERR-002", "Error occurred while sending email: " + error.getMessage(), error);
                    latch.countDown(); // Libera em caso de erro
                }
            );

            // Aguarda até a conclusão ou timeout
            boolean completed = latch.await(timeoutSeconds, TimeUnit.SECONDS);
            if (!completed) {
                log.warn("Timeout: Email operation did not complete within " + timeoutSeconds + " seconds");
                throw new PancakeException("PANCAKE-ERR-003", "Timeout: Email operation did not complete within " + timeoutSeconds + " seconds");
            }

            // Verifica se houve exceção assíncrona
            if (asyncException[0] != null) {
                throw asyncException[0];
            }

            // Define o status baseado se houve emails inválidos
            if (responseDto.getDetails().isEmpty()) {
                responseDto.setErrorCode("0");
                responseDto.setStatus("SUCCESS");
                responseDto.setMessage("Message sent successfully to all recipients");
            }

            log.info("Email operation finished.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted: " + e.getMessage());
            throw new PancakeException("PANCAKE-ERR-004", "Thread interrupted while sending email", e);
        } catch (PancakeException e) {
            throw e; // Relança PancakeException
        } catch (Exception exception) {
            log.error("Unexpected error: " + exception.getMessage());
            throw new PancakeException("PANCAKE-ERR-005", "Unexpected error while sending email: " + exception.getMessage(), exception);
        }
    }
}