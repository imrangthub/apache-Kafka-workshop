package com.imranmadbar.deadLetterTopic;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
import com.imranmadbar.Book;



@Configuration
public class DeadLetterListenerConfig {



   
	@Bean
	public DefaultErrorHandler customErrorHandlerE1() {
	    // Set up a backoff policy for retrying failed messages - 2-second interval, 3 retries
	    FixedBackOff backOff = new FixedBackOff(5000L, 3); 
	
	    DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, exception) -> {
	        // Custom logic for handling the error after retries are exhausted
	      System.out.println("Max retries exhausted, now MovingToDLQ");
	        
	      DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
			kafkaTemplate(),
	        (ConsumerRecord<?, ?> record, Exception e) -> 
	            new TopicPartition(record.topic() + ".DLT", record.partition()));
	        
	        System.out.println("MagThatFail: " + consumerRecord.value());
	        System.out.println("ErrMsg: " + exception.getMessage());
	    }, backOff);
	
	    // Add a listener to track each retry attempt
	    errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
	        System.out.println("RetryAttempt " + deliveryAttempt + " forMsg: " + record.value());
	        System.out.println("ErrorWas: " + ex.getMessage());
	        System.out.println("Waiting " + backOff.getInterval() + "ms before next retry...");
	        System.err.println();
	        System.err.println();
	    });
	
	    return errorHandler;
	}

    @Bean
    public DefaultErrorHandler customErrorHandlerE2() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        		kafkaTemplate(),
                (ConsumerRecord<?, ?> record, Exception e) -> 
                    new TopicPartition(record.topic() + ".DLT", record.partition())
        );
        return new DefaultErrorHandler(recoverer, new FixedBackOff(5000L, 3L));
    }
	
    

    @Bean
    public DefaultErrorHandler customErrorHandlerE3() {
        // Configure a 5-second backoff and max 3 retries
        FixedBackOff backOff = new FixedBackOff(5000L, 3);

        // Configure the DeadLetterPublishingRecoverer to send messages to a .DLT topic
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        		kafkaTemplate(),
            (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        // Create the error handler with the DeadLetterPublishingRecoverer and backoff strategy
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // Listener to track each retry attempt
        errorHandler.setRetryListeners((record, exception, deliveryAttempt) -> {
            System.out.println("Retry attempt " + deliveryAttempt + " for message: " + record.value());
            System.out.println("Error: " + exception.getMessage());
            System.out.println("Waiting " + backOff.getInterval() + "ms before next retry...");
            System.out.println();
        });

        return errorHandler;
    }

	
    @Bean
    public ConsumerFactory<String, Book> myConsumerFactory() {
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
    public ConcurrentKafkaListenerContainerFactory<String, Book> kafkaListenerContainerFactory1() {
        ConcurrentKafkaListenerContainerFactory<String, Book> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(myConsumerFactory());
        factory.setCommonErrorHandler(customErrorHandlerE3());
        factory.getContainerProperties().setAckMode(AckMode.RECORD);
        return factory;
    }
    
    
	
    @Bean
    public ProducerFactory<String, String> myProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // Ensures all replicas acknowledge receipt
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // Number of retries on send failure
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10); // Adds a small delay to batch requests
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864); // Total memory available to the producer for buffering
        configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 30000); // Max time to block on send
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Ensures exactly-once delivery
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip"); // Compression type (e.g., none, gzip, snappy, lz4, zstd)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        return new DefaultKafkaProducerFactory<>(configProps);

        
        
    }
    


    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(myProducerFactory());
    }
    
    



    

}