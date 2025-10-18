package ru.practicum.mapper.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.events.hub.HubEventType;
import ru.practicum.kafka.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
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
    protected SpecificRecordBase mapToAvroPayload(HubEventProto event) {
        ScenarioAddedEventProto e = event.getScenarioAdded();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(e.getName())
                .setConditions(conditionMapper.mapProtoToAvro(e.getConditionList()))
                .setActions(actionMapper.mapProtoToAvro(e.getActionList()))
                .build();
    }

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
