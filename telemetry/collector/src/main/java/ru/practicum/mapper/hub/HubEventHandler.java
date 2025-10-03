package ru.practicum.mapper.hub;

import ru.practicum.events.hub.HubEvent;
import ru.practicum.events.hub.HubEventType;

public interface HubEventHandler {
    HubEventType getMessageType();

    void handle(HubEvent event);
}
