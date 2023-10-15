package com.imran;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SmSProducer {

    String topicName = "topic-1";
    KafkaProducer<String,String> kafkaProducer;

    public SmSProducer(Map<String,Object> propsMap){

        kafkaProducer = new KafkaProducer<String, String>(propsMap);
    }

    public static Map<String,Object> propsMap(){

        Map<String,Object> propsMap  = new HashMap<>();
     //   propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
        propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return propsMap;
    }

    public void publishMessageSync(String key, String value){
        ProducerRecord<String,String> producerRecord = new ProducerRecord<>(topicName, key, value);

        
        
        try {
            RecordMetadata recordMetadata = kafkaProducer.send(producerRecord).get();
            System.out.println("partition" + recordMetadata.partition() +" , offset : " + recordMetadata.offset());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

    	SmSProducer messageProducer = new SmSProducer(propsMap());
        messageProducer.publishMessageSync("key5", "value5");
    }
}