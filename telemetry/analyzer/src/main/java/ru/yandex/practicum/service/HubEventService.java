package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Transactional
    public void handleEvent(HubEventAvro event) {
        String hubId = event.getHubId();

        switch (event.getPayload()) {
            case DeviceAddedEventAvro deviceAdded -> handleDeviceAdded(hubId, deviceAdded);
            case DeviceRemovedEventAvro deviceRemoved -> handleDeviceRemoved(hubId, deviceRemoved);
            case ScenarioAddedEventAvro scenarioAdded -> handleScenarioAdded(hubId, scenarioAdded);
            case ScenarioRemovedEventAvro scenarioRemoved -> handleScenarioRemoved(hubId, scenarioRemoved);
            default -> log.warn("Неизвестный тип payload");
        }
    }

    private void handleDeviceAdded(String hubId, DeviceAddedEventAvro device) {
        if (!sensorRepository.existsById(device.getId())) {
            Sensor newDevice = Sensor.builder()
                    .id(device.getId())
                    .hubId(hubId)
                    .build();
            sensorRepository.save(newDevice);
        }
    }

    private void handleDeviceRemoved(String hubId, DeviceRemovedEventAvro device) {
        sensorRepository.findByIdAndHubId(device.getId(), hubId).ifPresent(sensorRepository::delete);
    }

    private void handleScenarioAdded(String hubId, ScenarioAddedEventAvro scenario) {
        Scenario newScenario = scenarioRepository.findByHubIdAndName(hubId, scenario.getName())
                .orElseGet(() -> Scenario.builder().hubId(hubId).name(scenario.getName()).build());
        scenarioRepository.save(newScenario);

        List<ScenarioCondition> oldConditions = scenarioConditionRepository.findByScenarioId(newScenario.getId());
        scenarioConditionRepository.deleteAll(oldConditions);
        List<ScenarioAction> oldActions = scenarioActionRepository.findByScenarioId(newScenario.getId());
        scenarioActionRepository.deleteAll(oldActions);

        for (ScenarioConditionAvro conditionAvro : scenario.getConditions()) {
            Condition condition = Condition.builder()
                    .type(conditionAvro.getType().name())
                    .value(valueConversion(conditionAvro.getValue()))
                    .operation(conditionAvro.getOperation().name())
                    .build();
            conditionRepository.save(condition);

            Sensor sensor = sensorRepository.findByIdAndHubId(conditionAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new IllegalStateException("Сенсор для данного состояния не найден"));

            ScenarioCondition scenarioCondition = new ScenarioCondition(
                    new ScenarioConditionId(newScenario.getId(), sensor.getId(), condition.getId()),
                    newScenario,
                    sensor,
                    condition
            );
            scenarioConditionRepository.save(scenarioCondition);
        }

        for (DeviceActionAvro actionAvro : scenario.getActions()) {
            Action action = Action.builder()
                    .type(actionAvro.getType().name())
                    .value(valueConversion(actionAvro.getValue()))
                    .build();
            actionRepository.save(action);

            Sensor sensor = sensorRepository.findByIdAndHubId(actionAvro.getSensorId(), hubId)
                    .orElseThrow(() -> new IllegalStateException("Сенсор для данного действия не найден"));
            ScenarioAction scenarioAction = new ScenarioAction(
                    new ScenarioActionId(newScenario.getId(), sensor.getId(), action.getId()),
                    newScenario,
                    sensor,
                    action
            );
            scenarioActionRepository.save(scenarioAction);
        }
    }

    private void handleScenarioRemoved(String hubId, ScenarioRemovedEventAvro scenario) {
        scenarioRepository.findByHubIdAndName(hubId, scenario.getName()).ifPresent(scenarioRepository::delete);
    }

    private Integer valueConversion(Object value) {
        return switch (value) {
            case null -> null;
            case Integer i -> i;
            case Boolean b -> b ? 1 : 0;
            default -> throw new IllegalArgumentException("Unexpected type: " + value.getClass());
        };
    }

}
