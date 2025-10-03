package ru.practicum.mapper.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.events.hub.DeviceAddedEvent;
import ru.practicum.events.hub.HubEvent;
import ru.practicum.events.hub.HubEventType;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.practicum.mapper.EnumMapper;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedHubEventHandler extends BaseHubEventHandler {
    public DeviceAddedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected SpecificRecordBase mapToAvro(HubEvent event) {
        DeviceAddedEvent e = (DeviceAddedEvent) event;
        return DeviceAddedEventAvro.newBuilder()
                .setId(e.getId())
                .setType(EnumMapper.map(e.getDeviceType(), DeviceTypeAvro.class))
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

}
