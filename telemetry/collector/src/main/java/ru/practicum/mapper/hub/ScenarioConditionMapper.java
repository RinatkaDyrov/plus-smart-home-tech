package ru.practicum.mapper.hub;

import org.springframework.stereotype.Component;
import ru.practicum.events.hub.ScenarioCondition;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;

@Component
public class ScenarioConditionMapper {
    public ScenarioConditionAvro mapToAvro(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(condition.getValue())
                .build();
    }

    public ScenarioConditionAvro mapProtoToAvro(ScenarioConditionProto condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(condition.getIntValue())
                .build();
    }

    public List<ScenarioConditionAvro> mapToAvro(List<ScenarioCondition> conditions) {
        return conditions.stream().map(this::mapToAvro).toList();
    }

    public List<ScenarioConditionAvro> mapProtoToAvro(List<ScenarioConditionProto> conditions) {
        return conditions.stream().map(this::mapProtoToAvro).toList();
    }
}