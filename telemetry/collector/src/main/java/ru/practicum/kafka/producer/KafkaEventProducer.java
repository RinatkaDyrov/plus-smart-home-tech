package ru.practicum.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, SpecificRecordBase> kafkaTemplate;

    public void send(String topic, SpecificRecordBase event) {
        kafkaTemplate.send(topic, event);
    }
}