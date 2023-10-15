package com.imranmadbar;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmSConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SmSConsumer.class);
    
    String topicName = "topic-1";
    KafkaConsumer<String, String> kafkaConsumer;

    public SmSConsumer(Map<String, Object> propsMap) {
        kafkaConsumer = new KafkaConsumer<String, String>(propsMap);
    }

    public static Map<String, Object> buildConsumerProperties() {

        Map<String, Object> propsMap = new HashMap<>();
      //  propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, "sms-cng-group1");
        return propsMap;
    }

    public void pollKafka() {
    	List<String> topics = Arrays.asList(topicName);
    	kafkaConsumer.subscribe(topics);
    	
   //     kafkaConsumer.subscribe(List.of(topicName));
        Duration timeOutDuration = Duration.of(100, ChronoUnit.MILLIS);
        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(timeOutDuration);
                consumerRecords.forEach((record) -> {
                    logger.info("Consume sms ! \nTopicName: {}\nKey: {}\nValue: {}\nPartion: {}",
                    		record.topic(),
                            record.key(), record.value(), record.partition());
                });
            }
        } catch (Exception e) {
            logger.error("Exception in pollKafka : " + e);
        } finally {
            kafkaConsumer.close();
        }

    }

    public static void main(String[] args) {
    	SmSConsumer messageConsumer = new SmSConsumer(buildConsumerProperties());
        messageConsumer.pollKafka();
    }
}