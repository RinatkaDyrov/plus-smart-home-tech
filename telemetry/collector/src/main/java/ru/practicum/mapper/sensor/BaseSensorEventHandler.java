package ru.practicum.mapper.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    protected final KafkaEventProducer producer;

    @Value("${kafka.topics.sensors}")
    private String topicSensor;

    protected abstract T mapToAvroPayload(SensorEventProto proto);

    protected SensorEventAvro mapToAvro(SensorEventProto proto) {
        if (!proto.getPayloadCase().name().equals(getType().name())) {
            throw new IllegalArgumentException("Неизвестное событие: " + proto.getPayloadCase());
        }

        T payload = mapToAvroPayload(proto);

        return SensorEventAvro.newBuilder()
                .setId(proto.getId())
                .setHubId(proto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
    }

    @Override
    public void handle(SensorEventProto proto) {
        SensorEventAvro avro = mapToAvro(proto);
        producer.sendWithReport(topicSensor, proto.getHubId(), avro);
        log.debug("Отправлен {} в {}: {}", avro.getClass().getSimpleName(), topicSensor, avro);
    }
}
