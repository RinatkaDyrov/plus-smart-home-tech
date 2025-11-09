package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Service
public class SnapshotService {
    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
    }
}
