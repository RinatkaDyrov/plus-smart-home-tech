package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ScenarioActionId implements Serializable {
    @Column(name = "scenario_id")
    private Long scenarioId;

    @Column(name = "sensor_id")
    private String sensorId;

    @Column(name = "condition_id")
    private Long conditionId;
}
