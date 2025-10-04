package ru.practicum.mapper.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.events.hub.HubEvent;
import ru.practicum.kafka.producer.KafkaEventProducer;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;

    @Value("${kafka.topics.hubs}")
    private String topicHub;

    protected abstract T mapToAvro(HubEvent event);

    @Override
    public void handle(HubEvent event) {
        T avro = mapToAvro(event);
        producer.sendWithReport(topicHub, event.getHubId(), avro);
        log.debug("Отправлен {} в {}: {}", avro.getClass().getSimpleName(), topicHub, avro);
    }
}