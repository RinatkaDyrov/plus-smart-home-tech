package ru.practicum.mapper;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.events.hub.*;
import ru.practicum.events.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.stream.Collectors;

@Component
public class DtoToAvroMapper {

    public SensorEventAvro mapToAvro(SensorEvent dto) {

        SpecificRecordBase payload = switch (dto) {
            case LightSensorEvent e -> mapToAvro(dto);
            case MotionSensorEvent e -> mapToAvro(dto);
            case SwitchSensorEvent e -> mapToAvro(dto);
            case ClimateSensorEvent e -> mapToAvro(dto);
            case TemperatureSensorEvent e -> mapToAvro(dto);
            default -> throw new IllegalStateException("Unexpected value: " + dto);
        };

        return SensorEventAvro.newBuilder()
                .setId(dto.getId())
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public LightSensorAvro mapToAvro(LightSensorEvent dto) {

        return LightSensorAvro.newBuilder()
                .setLinkQuality(dto.getLinkQuality())
                .setLuminosity(dto.getLuminosity())
                .build();
    }

    public MotionSensorAvro mapToAvro(MotionSensorEvent dto) {

        return MotionSensorAvro.newBuilder()
                .setLinkQuality(dto.getLinkQuality())
                .setMotion(dto.getMotion())
                .setVoltage(dto.getVoltage())
                .build();
    }

    public SwitchSensorAvro mapToAvro(SwitchSensorEvent dto) {

        return SwitchSensorAvro.newBuilder()
                .setState(dto.getState())
                .build();
    }

    public ClimateSensorAvro mapToAvro(ClimateSensorEvent dto) {

        return ClimateSensorAvro.newBuilder()
                .setCo2Level(dto.getCo2Level())
                .setHumidity(dto.getHumidity())
                .setTemperatureC(dto.getTemperatureC())
                .build();
    }

    public TemperatureSensorAvro mapToAvro(TemperatureSensorEvent dto) {
        return TemperatureSensorAvro.newBuilder()
                .setId(dto.getId())
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setTemperatureC(dto.getTemperatureC())
                .setTemperatureF(dto.getTemperatureF())
                .build();
    }

    public HubEventAvro mapToAvro(HubEvent dto) {
        SpecificRecordBase payload = switch (dto) {
            case DeviceAddedEvent e -> mapToAvro(dto);
            case DeviceRemovedEvent e -> mapToAvro(dto);
            case ScenarioAddedEvent e -> mapToAvro(dto);
            case ScenarioRemovedEvent e -> mapToAvro(dto);
            default -> throw new IllegalStateException("Unexpected value: " + dto);
        };

        return HubEventAvro.newBuilder()
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public DeviceAddedEventAvro mapToAvro(DeviceAddedEvent dto) {

        return DeviceAddedEventAvro.newBuilder()
                .setId(dto.getId())
                .setType(DeviceTypeAvro.valueOf(dto.getDeviceType().name()))
                .build();
    }

    public DeviceRemovedEventAvro mapToAvro(DeviceRemovedEvent dto) {

        return DeviceRemovedEventAvro.newBuilder()
                .setId(dto.getId())
                .build();
    }

    public ScenarioAddedEventAvro mapToAvro(ScenarioAddedEvent dto) {

        return ScenarioAddedEventAvro.newBuilder()
                .setName(dto.getName())
                .setConditions(dto.getConditions().stream().map(this::mapToAvro).collect(Collectors.toList()))
                .setActions(dto.getActions().stream().map(this::mapToAvro).collect(Collectors.toList()))
                .build();
    }

    public DeviceActionAvro mapToAvro(DeviceAction dto) {

        return DeviceActionAvro.newBuilder()
                .setSensorId(dto.getSensorId())
                .setType(ActionTypeAvro.valueOf(dto.getType().name()))
                .setValue(dto.getValue())
                .build();
    }

    public ScenarioConditionAvro mapToAvro(ScenarioCondition dto) {

        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(dto.getSensorId())
                .setType(ConditionTypeAvro.valueOf(dto.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(dto.getOperation().name()));

        switch (dto.getValue()) {
            case null -> builder.setValue(null);
            case Boolean b -> builder.setValue(dto.getValue());
            case Integer i -> builder.setValue(dto.getValue());
            case Long l -> builder.setValue(dto.getValue());
            default -> throw new IllegalStateException("Неподдерживаемый тип значения: " +
                    dto.getValue().getClass().getName());
        }

        return builder.build();
    }

    public ScenarioRemovedEventAvro mapToAvro(ScenarioRemovedEvent dto) {

        return ScenarioRemovedEventAvro.newBuilder()
                .setName(dto.getName())
                .build();
    }
}
