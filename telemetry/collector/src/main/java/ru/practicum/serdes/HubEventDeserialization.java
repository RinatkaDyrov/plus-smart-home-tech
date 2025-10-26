package ru.practicum.serdes;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventDeserialization extends BaseAvroDeserializer<HubEventAvro> {

    public HubEventDeserialization() {
        super(HubEventAvro.getClassSchema());
    }
}
