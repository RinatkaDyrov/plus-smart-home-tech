package ru.practicum.mapper.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.events.sensor.LightSensorEvent;
import ru.practicum.events.sensor.SensorEvent;
import ru.practicum.events.sensor.SensorEventType;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler extends BaseSensorEventHandler {
    public LightSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected SpecificRecordBase mapToAvroPayload(SensorEvent event) {
        LightSensorEvent e = (LightSensorEvent) event;

        return LightSensorAvro.newBuilder()
                .setLuminosity(e.getLuminosity())
                .setLinkQuality(e.getLinkQuality())
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
