package com.inventory.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

@TestConfiguration
@EmbeddedKafka(partitions = 1, brokerProperties = {
    "listeners=PLAINTEXT://localhost:9092",
    "port=9092"
})
public class TestConfig {

    @Bean
    public KafkaTemplate<String, Object> testKafkaTemplate(org.springframework.kafka.test.EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        producerProps.put("key.serializer", StringSerializer.class);
        producerProps.put("value.serializer", JsonSerializer.class);
        DefaultKafkaProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public DefaultKafkaConsumerFactory<String, Object> testConsumerFactory(org.springframework.kafka.test.EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put("key.deserializer", StringDeserializer.class);
        consumerProps.put("value.deserializer", JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.inventory.event,com.booking.event");
        return new DefaultKafkaConsumerFactory<>(consumerProps);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(org.springframework.kafka.test.EmbeddedKafkaBroker embeddedKafkaBroker) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(testConsumerFactory(embeddedKafkaBroker));
        return factory;
    }
}