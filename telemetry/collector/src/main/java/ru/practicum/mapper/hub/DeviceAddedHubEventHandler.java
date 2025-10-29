package ru.practicum.mapper.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.events.hub.DeviceType;
import ru.practicum.events.hub.HubEventType;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedHubEventHandler extends BaseHubEventHandler {
    public DeviceAddedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    protected SpecificRecordBase mapToAvroPayload(HubEventProto event) {
        DeviceAddedEventProto e = event.getDeviceAdded();
        return DeviceAddedEventAvro.newBuilder()
                .setId(e.getId())
                .setType(EnumMapper.map(DeviceType.valueOf(e.getType().name()), DeviceTypeAvro.class))
                .build();
    }

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

}
