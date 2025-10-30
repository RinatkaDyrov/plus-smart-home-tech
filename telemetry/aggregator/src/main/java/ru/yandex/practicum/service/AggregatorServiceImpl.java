package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AggregatorServiceImpl implements AggregatorService {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro sensorsSnapshotAvro = snapshots.computeIfAbsent(event.getHubId(), hubId ->
                SensorsSnapshotAvro.newBuilder()
                        .setHubId(hubId)
                        .setSensorsState(new HashMap<>())
                        .build()
        );

        if (sensorsSnapshotAvro.getSensorsState().containsKey(event.getId())) {
            SensorStateAvro oldState = sensorsSnapshotAvro.getSensorsState().get(event.getId());
            if (event.getTimestamp().isBefore(oldState.getTimestamp()) || oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }

        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setData(event.getPayload())
                .setTimestamp(event.getTimestamp())
                .build();

        sensorsSnapshotAvro.getSensorsState().put(event.getId(), newState);
        sensorsSnapshotAvro.setTimestamp(newState.getTimestamp());
        snapshots.put(sensorsSnapshotAvro.getHubId(), sensorsSnapshotAvro);

        return Optional.of(sensorsSnapshotAvro);
    }
}
