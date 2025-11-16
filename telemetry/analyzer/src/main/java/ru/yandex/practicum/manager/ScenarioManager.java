package ru.yandex.practicum.manager;

import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.client.HubRouterClient;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioManager {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final SensorConditionManager sensorConditionManager;
    private final HubRouterClient client;

    public void evaluateSnapshot(SensorsSnapshotAvro snapshot) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(snapshot.getHubId());
        for (Scenario scenario : scenarios) {
            List<ScenarioCondition> conditions = scenarioConditionRepository.findByScenarioId(scenario.getId());

            boolean conditionOk = conditions.stream()
                    .allMatch(sc -> {
                        String sensorId = sc.getSensor().getId();
                        if (!snapshot.getSensorsState().containsKey(sensorId)) {
                            return false;
                        }
                        return sensorConditionManager.extractValue(
                                ConditionType.valueOf(sc.getCondition().getType()),
                                snapshot.getSensorsState().get(sensorId)
                        )
                                .map(val -> sensorConditionManager.compare(
                                        Operation.valueOf(sc.getCondition().getOperation()),
                                        val,
                                        BigDecimal.valueOf(sc.getCondition().getValue())
                                ))
                                .orElse(false);
                    });

            if (conditionOk) {
                List<ScenarioAction> actions = scenarioActionRepository.findByScenarioId(scenario.getId());

                actions.forEach(a -> {
                    Action action = a.getAction();
                    Sensor sensor = a.getSensor();

                    DeviceActionProto actionProto = sensorConditionManager.buildAction(
                            ActionType.valueOf(action.getType()),
                            sensor.getId(),
                            action.getValue());

                    DeviceActionRequest request = DeviceActionRequest.newBuilder()
                            .setHubId(scenario.getHubId())
                            .setScenarioName(scenario.getName())
                            .setAction(actionProto)
                            .setTimestamp(Timestamps.fromMillis(snapshot.getTimestamp().toEpochMilli()))
                            .build();

                    client.sendAction(request);
                });
            }
        }
    }
}
