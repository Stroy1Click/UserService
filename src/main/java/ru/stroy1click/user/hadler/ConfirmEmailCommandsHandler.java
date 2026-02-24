package ru.stroy1click.user.hadler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.stroy1click.common.command.ConfirmEmailCommand;
import ru.stroy1click.outbox.consumer.entity.ProcessedEvent;
import ru.stroy1click.outbox.consumer.service.ProcessedEventService;
import ru.stroy1click.user.service.UserService;

@Slf4j
@Component
@KafkaListener(topics = {"confirm-email-commands"})
@RequiredArgsConstructor
public class ConfirmEmailCommandsHandler {

    private final UserService userService;

    private final ProcessedEventService processedEventService;

    @KafkaHandler
    @Transactional
    public void handle(@Header(name = "messageId") byte[] messageIdBytes, @Payload ConfirmEmailCommand command){
        log.info("handle {}", command.getEmail());

        //сразу в Long нельзя, так как автоматически не преобразовывается
        Long messageId = Long.valueOf(new String(messageIdBytes));

        if(this.processedEventService.findByMessageId(messageId).isPresent()){
            log.warn("Event with messageId {} already processed. Skipping.", messageId);
            return;
        }

        this.userService.updateEmailConfirmedStatus(command.getEmail());

        this.processedEventService.save(new ProcessedEvent(null, messageId));
    }
}
