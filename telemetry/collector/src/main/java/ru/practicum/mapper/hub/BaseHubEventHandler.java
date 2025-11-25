package ru.practicum.mapper.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;

    @Value("${kafka.topics.hubs}")
    private String topicHub;

    protected abstract T mapToAvroPayload(HubEventProto event);

    protected HubEventAvro mapToAvro(HubEventProto proto) {
        if (!proto.getPayloadCase().name().equals(getType().name())) {
            throw new IllegalArgumentException("Неизвестное событие: " + proto.getPayloadCase());
        }

        T payload = mapToAvroPayload(proto);

        return HubEventAvro.newBuilder()
                .setHubId(proto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }


    @Override
    public void handle(HubEventProto event) {
        HubEventAvro avro = mapToAvro(event);
        producer.sendWithReport(topicHub, event.getHubId(), avro);
        log.debug("Отправлен {} в {}: {}", avro.getClass().getSimpleName(), topicHub, avro);
    }
}