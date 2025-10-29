package ru.practicum.mapper.hub;

import ru.practicum.events.hub.HubEventType;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandler {
    HubEventType getType();

    void handle(HubEventProto event);
}
