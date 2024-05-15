package com.kafkapress.kafkapress.producer;


import com.kafkapress.kafkapress.config.KafkaConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class ProducerConfig {
    private KafkaConfig kafkaConfigModel;
    @Autowired
    public ProducerConfig(KafkaConfig kafkaConfigModel){
        this.kafkaConfigModel=kafkaConfigModel;
    }
    @Bean
    public ProducerFactory<String, String> defaultProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaConfigModel.getServers());
        config.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 一次发送的消息大小 单位为byte
        config.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG,kafkaConfigModel.getBatchSize() * 1024);

        return new DefaultKafkaProducerFactory<>(config);

    }
    @Bean
    public KafkaTemplate<String, String> defaultKafkatemplate() {
        return new KafkaTemplate<>(defaultProducerFactory());
    }
}
