package com.vlz.laborexchange_authservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserRegistrationProducer extends AbstractProducer<RegisterRequest>  {

    @Value("${spring.kafka.topics.user-registration}")
    private String userRegistrationTopicName;

    public UserRegistrationProducer(KafkaTemplate<String, String> kafkaTemplate,  ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    public void send(RegisterRequest event) {
        super.sendMessage(userRegistrationTopicName, event);
        log.info("Sending user registration event: {}", event);
    }
}
