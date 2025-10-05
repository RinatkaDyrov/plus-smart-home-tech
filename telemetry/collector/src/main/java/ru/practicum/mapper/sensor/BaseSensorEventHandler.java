package ru.practicum.mapper.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.events.sensor.SensorEvent;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    protected final KafkaEventProducer producer;

    @Value("${kafka.topics.sensors}")
    private String topicSensor;

    protected abstract T mapToAvroPayload(SensorEvent event);

    protected SensorEventAvro mapToAvro(SensorEvent event) {
        if (!event.getType().equals(getType())) {
            throw new IllegalArgumentException("Неизвестное событие: " + event.getType());
        }

        T payload = mapToAvroPayload(event);

        return SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
    }

    @Override
    public void handle(SensorEvent event) {
        SensorEventAvro avro = mapToAvro(event);
        producer.sendWithReport(topicSensor, event.getHubId(), avro);
        log.debug("Отправлен {} в {}: {}", avro.getClass().getSimpleName(), topicSensor, avro);
    }
}
