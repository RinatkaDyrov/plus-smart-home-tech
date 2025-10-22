package ru.practicum.serdes;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;


public class BaseAvroDeserializer <T extends SpecificRecordBase> implements Deserializer<T> {
    private final DatumReader<T> reader = new SpecificDatumReader<>();  //Какой-то класс надо въебать, было вот так new SpecificDatumReader<>(SensorEventAvro.getClassSchema());
    public BaseAvroDeserializer(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseAvroDeserializer(DecoderFactory decoderFactory, Schema schema) {
        // ...
    }
    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            if (data != null) {
                BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
                return this.reader.read(null, decoder);
            }
            return null;
        } catch (Exception e) {
            throw new DeserializationException("Ошибка десереализации данных из топика [" + topic + "]", e);
        }
    }
}
