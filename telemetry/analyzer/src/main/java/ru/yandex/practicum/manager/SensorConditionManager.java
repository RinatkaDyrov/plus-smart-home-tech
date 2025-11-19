package ru.yandex.practicum.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.ActionType;
import ru.yandex.practicum.model.ConditionType;
import ru.yandex.practicum.model.Operation;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SensorConditionManager {

    private final Map<ConditionType, Function<SensorStateAvro, Optional<BigDecimal>>> extractors =
            Map.of(
                    ConditionType.TEMPERATURE, state -> switch (state.getData()) {
                        case TemperatureSensorAvro temp -> Optional.of(BigDecimal.valueOf(temp.getTemperatureC()));
                        case ClimateSensorAvro climate -> Optional.of(BigDecimal.valueOf(climate.getTemperatureC()));
                        case null, default -> Optional.empty();
                    },
                    ConditionType.HUMIDITY, state -> switch (state.getData()) {
                        case ClimateSensorAvro climate -> Optional.of(BigDecimal.valueOf(climate.getHumidity()));
                        case null, default -> Optional.empty();
                    },
                    ConditionType.CO2LEVEL, state -> switch (state.getData()) {
                        case ClimateSensorAvro climate -> Optional.of(BigDecimal.valueOf(climate.getCo2Level()));
                        case null, default -> Optional.empty();
                    },
                    ConditionType.LUMINOSITY, state -> switch (state.getData()) {
                        case LightSensorAvro light -> Optional.of(BigDecimal.valueOf(light.getLuminosity()));
                        case null, default -> Optional.empty();
                    },
                    ConditionType.MOTION, state -> switch (state.getData()) {
                        case MotionSensorAvro motion ->
                                Optional.of(motion.getMotion() ? BigDecimal.ONE : BigDecimal.ZERO);
                        case null, default -> Optional.empty();
                    },
                    ConditionType.SWITCH, state -> switch (state.getData()) {
                        case SwitchSensorAvro sw -> Optional.of(sw.getState() ? BigDecimal.ONE : BigDecimal.ZERO);
                        case null, default -> Optional.empty();
                    }
            );


    private final Map<Operation, BiPredicate<BigDecimal, BigDecimal>> comparators =
            Map.of(
                    Operation.GREATER_THAN, (a, b) -> a.compareTo(b) > 0,
                    Operation.LOWER_THAN, (a, b) -> a.compareTo(b) < 0,
                    Operation.EQUALS, (a, b) -> a.compareTo(b) == 0
            );

    private final Map<ActionType, BiFunction<String, Integer, DeviceActionProto>> actionBuilders =
            Map.of(
                    ActionType.ACTIVATE, (sensorId, value) ->
                            DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.ACTIVATE)
                                    .build(),
                    ActionType.DEACTIVATE, (sensorId, value) ->
                            DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.DEACTIVATE)
                                    .build(),
                    ActionType.INVERSE, (sensorId, value) ->
                            DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.INVERSE)
                                    .build(),
                    ActionType.SET_VALUE, (sensorId, value) ->
                            DeviceActionProto.newBuilder()
                                    .setSensorId(sensorId)
                                    .setType(ActionTypeProto.SET_VALUE)
                                    .setValue(value)
                                    .build()
            );

    public Optional<BigDecimal> extractValue(ConditionType type, SensorStateAvro state) {
        return extractors.getOrDefault(type, st -> Optional.empty()).apply(state);
    }

    public boolean compare(Operation operation, BigDecimal left, BigDecimal right) {
        return comparators.getOrDefault(operation, (a, b) -> false).test(left, right);
    }

    public DeviceActionProto buildAction(ActionType type, String sensorId, Integer value) {
        return actionBuilders
                .getOrDefault(type, (id, v) -> DeviceActionProto.getDefaultInstance())
                .apply(sensorId, value);
    }
}
