package ru.practicum.mapper.hub;

import org.springframework.stereotype.Component;
import ru.practicum.events.hub.DeviceAction;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

import java.util.List;

@Component
public class DeviceActionMapper {

    public DeviceActionAvro mapToAvro(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(ActionTypeAvro.valueOf(action.getType().name()))
                .setValue(action.getValue())
                .build();
    }

    public List<DeviceActionAvro> mapToAvro(List<DeviceAction> actions) {
        return actions.stream().map(this::mapToAvro).toList();
    }
}