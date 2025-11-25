package ru.practicum.eventDeserializer;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorSnapshotEventDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {

    public SensorSnapshotEventDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
