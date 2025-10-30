package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.AggregatorService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private static final String SENSOR_TOPIC = "telemetry.sensors.v1";
    private static final String SENSOR_SNAPSHOT_TOPIC = "telemetry.snapshots.v1";
    private static final long POLL_TIMEOUT = 1;

    private final Consumer<String, SpecificRecordBase> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final AggregatorService service;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(SENSOR_TOPIC));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofSeconds(POLL_TIMEOUT));
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    service.updateState((SensorEventAvro) record.value()).ifPresent(snapshot ->
                            producer.send(new ProducerRecord<>(
                                    SENSOR_SNAPSHOT_TOPIC,
                                    null,
                                    snapshot.getTimestamp().toEpochMilli(),
                                    snapshot.getHubId(),
                                    snapshot)));
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
            log.warn("Прервано ожидание потока {}", Thread.currentThread().getName());
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                log.info("Сбрасываем буфер продюсера через flush()");
                producer.flush();
                log.info("Делаем финальный commitSync() оффсетов");
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }

    }
}