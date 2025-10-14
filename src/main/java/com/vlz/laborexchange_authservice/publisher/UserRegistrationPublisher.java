package com.vlz.laborexchange_authservice.publisher;

import com.vlz.laborexchange_authservice.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserRegistrationPublisher implements EventPublisher<RegisterRequest> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public UserRegistrationPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                     @Value("${topics.user-registration}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(RegisterRequest event) {
        kafkaTemplate.send(topic, event);
        log.info("Published user registration event for email: {}", event.getEmail());
    }
}
