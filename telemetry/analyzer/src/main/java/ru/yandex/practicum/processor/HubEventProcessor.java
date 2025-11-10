package ru.yandex.practicum.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubEventService;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    @Value("${topics.hubs}")
    private String topics;
    private final Properties hubProperties;
    private final HubEventService hubEventService;

    private Consumer<String, HubEventAvro> consumer;
    private boolean isRunning = true;

    @Override
    public void run() {
        consumer = new KafkaConsumer<>(hubProperties);
        consumer.subscribe(List.of(topics));
        try {
            while (isRunning) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    log.info("Получено событие {} от хаба {}", event, event.getHubId());
                    hubEventService.handleEvent(event);
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
        log.info("HubEventProcessor прекращает работу");
        isRunning = false;
        if (consumer != null) {
            consumer.wakeup();
        }
    }
}