package com.imranmadbar.customErrorHandlerTopic;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import com.imranmadbar.Book;

@Configuration
public class CustomErrorListenerConfig {

   @Bean
    DefaultErrorHandler customErrorHandler1() {
        return new DefaultErrorHandler((rec, ex) -> {
            System.out.println("ErrorFound########################################: " + rec);
        }, new FixedBackOff(0L, 0L));
    }
   
   
	   
	   
    @Bean
    public ConsumerFactory<String, Book> myConsumerFactory1() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // Reset to latest offset on offset out-of-range exceptions
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 200); // Maximum number of records per poll
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000); // Interval at which the consumer sends heartbeats
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000); // The maximum time between poll invocations before the consumer will be considered dead
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Allow deserialization of all packages   
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(), // Use StringDeserializer for keys
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(Book.class))
        );
    }

    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Book> kafkaListenerContainerFactory2() {
        ConcurrentKafkaListenerContainerFactory<String, Book> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(myConsumerFactory1());
        factory.setCommonErrorHandler(customErrorHandler1());
        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        return factory;
    }
    
    

    


}