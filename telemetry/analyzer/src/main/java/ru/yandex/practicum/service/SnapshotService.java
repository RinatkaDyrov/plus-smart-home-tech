package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.manager.ScenarioManager;

@Service
@RequiredArgsConstructor
public class SnapshotService {
    private final ScenarioManager scenarioManager;

    @Transactional
    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        scenarioManager.evaluateSnapshot(snapshot);
    }
}
