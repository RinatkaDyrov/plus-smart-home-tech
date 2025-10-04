package ru.practicum.mapper.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.events.hub.HubEvent;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;

    @Value("${kafka.topics.hubs}")
    private String topicHub;

    protected abstract T mapToAvroPayload(HubEvent event);

    protected HubEventAvro mapToAvro(HubEvent event) {
        if (!event.getType().equals(getType())) {
            throw new IllegalArgumentException("Неизвестное событие: " + event.getType());
        }

        T payload = mapToAvroPayload(event);

        return HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }


    @Override
    public void handle(HubEvent event) {
        HubEventAvro avro = mapToAvro(event);
        producer.sendWithReport(topicHub, event.getHubId(), avro);
        log.debug("Отправлен {} в {}: {}", avro.getClass().getSimpleName(), topicHub, avro);
    }
}