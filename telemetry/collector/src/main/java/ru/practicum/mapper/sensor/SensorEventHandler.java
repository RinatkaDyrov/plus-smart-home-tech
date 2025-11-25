package ru.practicum.mapper.sensor;

import ru.practicum.events.sensor.SensorEventType;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventType getType();

    void handle(SensorEventProto proto);
}
