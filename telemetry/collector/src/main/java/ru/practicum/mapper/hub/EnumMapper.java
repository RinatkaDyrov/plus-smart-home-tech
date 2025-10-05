package ru.practicum.mapper.hub;

import ru.practicum.events.hub.DeviceType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

public class EnumMapper {
    public static DeviceTypeAvro map(DeviceType deviceType, Class<DeviceTypeAvro> deviceTypeAvroClass) {
        if (deviceType == null) {
            return null;
        }
        try {
            return Enum.valueOf(deviceTypeAvroClass, deviceType.name());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Нет соответствующего типа для "
                    + deviceType + " среди " + deviceTypeAvroClass.getSimpleName(), ex);
        }
    }
}
