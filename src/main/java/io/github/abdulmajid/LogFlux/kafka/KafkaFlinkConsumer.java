package io.github.abdulmajid.LogFlux.kafka;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class KafkaFlinkConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaFlinkConsumer.class);

    public static void main(String[] args) {
        startConsuming();
    }

    public static void startConsuming() {
        try {
            StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
            environment.enableCheckpointing(5000);

            Properties consumerConfig = new Properties();
            try (InputStream stream = KafkaFlinkConsumer.class.getClassLoader().getResourceAsStream("consumer.properties")) {
                if (stream != null) {
                    consumerConfig.load(stream);
                } else {
                    LOG.warn("consumer.properties file not found, using default Kafka configuration.");
                }
            }

            KafkaSource<String> source = KafkaSource.<String>builder()
                .setProperties(consumerConfig) // Load properties from consumer.properties
                .setTopics("celery-logs")
                .setGroupId("flink-consumer-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

            DataStream<String> dataStream = environment.fromSource(
                source,
                WatermarkStrategy.noWatermarks(),
                "Kafka Source"
            );

            LOG.info("Start consuming from Kafka topic: celery-logs");
            dataStream.print();

            environment.execute("Kafka to Flink Consumer Job");
        } catch (Exception e) {
            LOG.error("Error while consuming from Kafka", e);
        }
    }
}
