package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.events.hub.HubEvent;
import ru.practicum.events.hub.HubEventType;
import ru.practicum.events.sensor.SensorEvent;
import ru.practicum.events.sensor.SensorEventType;
import ru.practicum.mapper.hub.HubEventHandler;
import ru.practicum.mapper.sensor.SensorEventHandler;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CollectorService {

    private final Map<HubEventType, HubEventHandler> hubHandlers;
    private final Map<SensorEventType, SensorEventHandler> sensorHandlers;

    public CollectorService(List<HubEventHandler> hubEventHandlers, List<SensorEventHandler> sensorEventHandlers) {

        this.hubHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getType,
                        h -> h,
                        (a, b) -> a,
                        () -> new EnumMap<>(HubEventType.class)
                ));
        this.sensorHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getType,
                        s -> s,
                        (a, b) -> a,
                        () -> new EnumMap<>(SensorEventType.class)
                ));
        log.info("Hub handlers registered: {}", hubHandlers.keySet());
    }

    public void send(SensorEvent event) {
        SensorEventHandler s = sensorHandlers.get(event.getType());
        if (s == null) {
            throw new IllegalArgumentException("No handler for " + event.getType());
        }
        s.handle(event);
    }

    public void send(HubEvent event) {
        HubEventHandler h = hubHandlers.get(event.getType());
        if (h == null) {
            throw new IllegalArgumentException("No handler for " + event.getType());
        }
        h.handle(event);
    }
}
