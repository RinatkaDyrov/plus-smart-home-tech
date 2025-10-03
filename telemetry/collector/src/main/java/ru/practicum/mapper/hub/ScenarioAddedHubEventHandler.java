package ru.practicum.mapper.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.events.hub.HubEvent;
import ru.practicum.events.hub.HubEventType;
import ru.practicum.events.hub.ScenarioAddedEvent;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.practicum.mapper.DeviceActionMapper;
import ru.practicum.mapper.ScenarioConditionMapper;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Component
public class ScenarioAddedHubEventHandler extends BaseHubEventHandler {
    private final ScenarioConditionMapper conditionMapper;
    private final DeviceActionMapper actionMapper;
    public ScenarioAddedHubEventHandler(KafkaEventProducer producer, ScenarioConditionMapper conditionMapper, DeviceActionMapper actionMapper) {
        super(producer);
        this.conditionMapper = conditionMapper;
        this.actionMapper = actionMapper;
    }


    @Override
    protected SpecificRecordBase mapToAvro(HubEvent event) {
        ScenarioAddedEvent e = (ScenarioAddedEvent) event;

        return ScenarioAddedEventAvro.newBuilder()
                .setName(e.getName())
                .setConditions(conditionMapper.mapToAvro(e.getConditions()))
                .setActions(actionMapper.mapToAvro(e.getActions()))
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
