package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.manager.ScenarioManager;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnapshotService {
    private final ScenarioManager scenarioManager;

    @Transactional
    public void handleSnapshot(SensorsSnapshotAvro snapshot) {
        log.info("Получен снапшот от хаба {} с {} сенсорами",
                snapshot.getHubId(), snapshot.getSensorsState().size());
        scenarioManager.evaluateSnapshot(snapshot);
    }
}
