package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.events.hub.HubEvent;
import ru.practicum.events.sensor.SensorEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorService {

    private final String topicSensor;
    private final String topicHub;

    public void send(SensorEvent event) {

    }

    public void send(HubEvent event) {

    }
}
