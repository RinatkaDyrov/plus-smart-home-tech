package ru.practicum.mapper.sensor;

import ru.practicum.events.sensor.SensorEvent;
import ru.practicum.events.sensor.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getType();

    void handle(SensorEvent event);
}
