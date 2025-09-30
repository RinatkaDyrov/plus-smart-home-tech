package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.events.hub.HubEvent;
import ru.practicum.events.sensor.SensorEvent;
import ru.practicum.mapper.DtoToAvroMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorService {

    @Value("${kafka.topics.sensors}")
    private String topicSensor;
    @Value("${kafka.topics.hubs}")
    private String topicHub;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void send(SensorEvent event) {
        byte[] data = serialize(DtoToAvroMapper.mapToAvro(event));
        sendWithReport(topicSensor, event.getHubId(), data);
    }

    public void send(HubEvent event) {
        byte[] data = serialize(DtoToAvroMapper.mapToAvro(event));
        sendWithReport(topicHub, event.getHubId(), data);
    }

    private byte[] serialize(SpecificRecord record) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            DatumWriter<SpecificRecord> datumWriter = new SpecificDatumWriter<>(record.getSchema());
            datumWriter.write(record, encoder);
            encoder.flush();

            return out.toByteArray();
        } catch (IOException e) {
            log.warn("Ошибка сериализации данных");
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendWithReport(String topic, String key, byte[] data) {
        kafkaTemplate.send(topic, key, data)
                .whenComplete(((stringSendResult, throwable) -> {
                    if (throwable != null) {
                        log.warn("Ошибка при отправке сообщения в топик {}: {}", topic, throwable.getMessage());
                    } else {
                        log.info("Сообщение отправлено в топик {}. Offset - {}",
                                topic, stringSendResult.getRecordMetadata().offset());
                    }
                }));
    }
}
