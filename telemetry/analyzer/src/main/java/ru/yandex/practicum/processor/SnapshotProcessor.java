package ru.yandex.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotService;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor implements Runnable {

    @Value("${topics.snapshots}")
    private String topics;

    private final SnapshotService snapshotService;

    @Qualifier("snapshotConsumerProperties")
    private final Properties snapshotConsumerProperties;

    private Consumer<String, SensorsSnapshotAvro> consumer;
    private boolean isRunning = true;

    @Override
    public void run() {
        consumer = new KafkaConsumer<>(snapshotConsumerProperties);
        consumer.subscribe(List.of(topics));
        try {
            while (isRunning) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    try {
                        SensorsSnapshotAvro snapshot = record.value();
                        log.info("Получено состояние {} от хаба {}", snapshot, snapshot.getHubId());
                        snapshotService.handleSnapshot(snapshot);
                    } catch (Exception exception) {
                        log.warn("Ошибка обработки состояния: {}", exception.getMessage());
                    }
                }
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException e) {
            log.warn("WakeupException: {}", e.getMessage());
        } finally {
            consumer.close();
            log.info("Потребитель закрыт");
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("SnapshotProcessor прекращает работу");
        isRunning = false;
        if (consumer != null) {
            consumer.wakeup();
        }
    }
}
