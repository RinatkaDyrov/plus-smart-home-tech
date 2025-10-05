package ru.practicum.mapper.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.events.sensor.SensorEvent;
import ru.practicum.events.sensor.SensorEventType;
import ru.practicum.events.sensor.TemperatureSensorEvent;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler {

    public TemperatureSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected SpecificRecordBase mapToAvroPayload(SensorEvent event) {
        TemperatureSensorEvent e = (TemperatureSensorEvent) event;

        return TemperatureSensorAvro.newBuilder()
                .setId(e.getId())
                .setHubId(e.getHubId())
                .setTimestamp(e.getTimestamp())
                .setTemperatureC(e.getTemperatureC())
                .setTemperatureF(e.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
