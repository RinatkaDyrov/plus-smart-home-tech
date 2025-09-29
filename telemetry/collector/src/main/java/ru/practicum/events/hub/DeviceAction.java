package ru.practicum.events.hub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceAction {

    private String sensorId;
    private DeviceType type;
    Integer value;
}
