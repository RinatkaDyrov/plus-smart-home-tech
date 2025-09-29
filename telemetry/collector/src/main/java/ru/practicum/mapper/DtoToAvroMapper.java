package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.events.hub.*;
import ru.practicum.events.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoToAvroMapper {

    public static SensorEventAvro mapToAvro(SensorEvent dto) {

        Object payload = switch (dto.getType()) {
            case LIGHT_SENSOR_EVENT -> mapToAvro((LightSensorEvent) dto);
            case MOTION_SENSOR_EVENT -> mapToAvro((MotionSensorEvent) dto);
            case SWITCH_SENSOR_EVENT -> mapToAvro((SwitchSensorEvent) dto);
            case CLIMATE_SENSOR_EVENT -> mapToAvro((ClimateSensorEvent) dto);
            case TEMPERATURE_SENSOR_EVENT -> mapToAvro((TemperatureSensorEvent) dto);
        };

        return SensorEventAvro.newBuilder()
                .setId(dto.getId())
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public static LightSensorAvro mapToAvro(LightSensorEvent dto) {

        return LightSensorAvro.newBuilder()
                .setLinkQuality(dto.getLinkQuality())
                .setLuminosity(dto.getLuminosity())
                .build();
    }

    public static MotionSensorAvro mapToAvro(MotionSensorEvent dto) {

        return MotionSensorAvro.newBuilder()
                .setLinkQuality(dto.getLinkQuality())
                .setMotion(dto.getMotion())
                .setVoltage(dto.getVoltage())
                .build();
    }

    public static SwitchSensorAvro mapToAvro(SwitchSensorEvent dto) {

        return SwitchSensorAvro.newBuilder()
                .setState(dto.getState())
                .build();
    }

    public static ClimateSensorAvro mapToAvro(ClimateSensorEvent dto) {

        return ClimateSensorAvro.newBuilder()
                .setCo2Level(dto.getCo2Level())
                .setHumidity(dto.getHumidity())
                .setTemperatureC(dto.getTemperatureC())
                .build();
    }

    public static TemperatureSensorAvro mapToAvro(TemperatureSensorEvent dto)
    {
        return TemperatureSensorAvro.newBuilder()
                .setId(dto.getId())
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setTemperatureC(dto.getTemperatureC())
                .setTemperatureF(dto.getTemperatureF())
                .build();
    }

    public static HubEventAvro mapToAvro(HubEvent dto) {
        Object payload = switch (dto.getType()) {
            case DEVICE_ADDED -> mapToAvro((DeviceAddedEvent) dto);
            case DEVICE_REMOVED -> mapToAvro((DeviceRemovedEvent) dto);
            case SCENARIO_ADDED -> mapToAvro((ScenarioAddedEvent) dto);
            case SCENARIO_REMOVED -> mapToAvro((ScenarioRemovedEvent) dto);
        };

        return HubEventAvro.newBuilder()
                .setHubId(dto.getHubId())
                .setTimestamp(dto.getTimestamp())
                .setPayload(payload)
                .build();
    }

    public static DeviceAddedEventAvro mapToAvro(DeviceAddedEvent dto) {

        return DeviceAddedEventAvro.newBuilder()
                .setId(dto.getId())
                .setType(DeviceTypeAvro.valueOf(dto.getType().name()))
                .build();
    }

    public static DeviceRemovedEventAvro mapToAvro(DeviceRemovedEvent dto) {

        return DeviceRemovedEventAvro.newBuilder()
                .setId(dto.getId())
                .build();
    }

    public static ScenarioAddedEventAvro mapToAvro(ScenarioAddedEvent dto) {

        List<DeviceActionAvro> actions = dto.getActions().stream()
                .map(DtoToAvroMapper::mapToAvro)
                .collect(Collectors.toList());
        List<ScenarioConditionAvro> conditions = dto.getCondition().stream()
                .map(DtoToAvroMapper::mapToAvro)
                .collect(Collectors.toList());

        return ScenarioAddedEventAvro.newBuilder()
                .setActions(actions)
                .setConditions(conditions)
                .setName(dto.getName())
                .build();
    }

    public static DeviceActionAvro mapToAvro(DeviceAction dto) {

        return DeviceActionAvro.newBuilder()
                .setType(ActionTypeAvro.valueOf(dto.getType().name()))
                .setSensorId(dto.getSensorId())
                .setValue(dto.getValue())
                .build();
    }

    public static ScenarioConditionAvro mapToAvro(ScenarioCondition dto) {
        return ScenarioConditionAvro.newBuilder()
                .setType(ConditionTypeAvro.valueOf(dto.getType().name()))
                .setValue(dto.getValue())
                .setOperation(ConditionOperationAvro.valueOf(dto.getOperation().name()))
                .setSensorId(dto.getSensorId())
                .build();
    }

    public static ScenarioRemovedEventAvro mapToAvro(ScenarioRemovedEvent dto) {

        return ScenarioRemovedEventAvro.newBuilder()
                .setName(dto.getName())
                .build();
    }
}
