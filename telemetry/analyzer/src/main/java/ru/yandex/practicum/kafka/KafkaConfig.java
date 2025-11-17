package ru.yandex.practicum.kafka;

import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@Data
public class KafkaConfig {


    @Value("${analyzer.kafka.bootstrap-server}")
    private String bootstrapServers;

    @Value("${analyzer.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${analyzer.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${analyzer.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;

    @Value("${analyzer.kafka.hub.group-id}")
    private String hubGroupId;

    @Value("${analyzer.kafka.hub.value-deserializer}")
    private String hubValueDeserializer;

    @Value("${analyzer.kafka.snapshot.group-id}")
    private String snapshotGroupId;

    @Value("${analyzer.kafka.snapshot.value-deserializer}")
    private String snapshotValueDeserializer;

    @Bean
    public Properties hubConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubValueDeserializer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, hubGroupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 200);
        return properties;
    }

    @Bean
    public Properties snapshotConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotValueDeserializer);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 200);
        return properties;
    }
}
