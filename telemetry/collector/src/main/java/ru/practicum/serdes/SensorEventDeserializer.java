package ru.practicum.serdes;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

public class SensorEventDeserializer extends BaseAvroDeserializer {
    private final DecoderFactory decoderFactory = DecoderFactory.get();
    private final DatumReader<SensorEventAvro> reader = new SpecificDatumReader<>(SensorEventAvro.getClassSchema());

    public SensorEventDeserializer() {
        super(SensorEventAvro.getClassSchema());
    }


    @Override
    public SensorEventAvro deserialize(String topic, byte[] data) {
        try {
            if (data != null) {
                BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);
                return this.reader.read(null, decoder);
            }
            return null;
        } catch (Exception e) {
            throw new DeserializationException("Ошибка десереализации данных из топика [" + topic + "]", e);
        }
    }
}