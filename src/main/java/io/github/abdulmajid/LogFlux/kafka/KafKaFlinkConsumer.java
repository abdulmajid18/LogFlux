package io.github.abdulmajid.LogFlux.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;

public class KafKaFlinkConsumer {
    public void startConsuming() throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
            .setBootstrapServers("localhost:9092") 
            .setTopics("celeryworker-topic")      
            .setGroupId("flink-consumer-group")   
            .setStartingOffsets(OffsetsInitializer.earliest()) 
            .setValueOnlyDeserializer(new SimpleStringSchema()) 
            .build();

        DataStream<String> dataStream = environment.fromSource(
            source,
            WatermarkStrategy.noWatermarks(),
            "Kafka Source"
        );

        dataStream.print();

        environment.execute("Kafka to Flink Consumer Job");
    }
}