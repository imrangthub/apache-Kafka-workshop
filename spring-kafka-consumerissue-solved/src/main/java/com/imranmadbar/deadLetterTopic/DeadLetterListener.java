package com.imranmadbar.deadLetterTopic;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.imranmadbar.Book;

@Service
public class DeadLetterListener {

	
    @KafkaListener(topics = "test-topic1.DLT", groupId = "my-consumer-group-dlq")
    public void listenToDLT(String obj) {
        System.out.println("DLTQueObj:##################: " + obj);
        // Handle the failed message here (logging, alerting, reprocessing, etc.)
    }
    
    
	@KafkaListener(topics = "test-topic1", groupId = "my-consumer-group", containerFactory = "kafkaListenerContainerFactory1")
	public void listen(Book obj) {
		System.out.println("ReceivedBookObj###############: " + obj);
    	throw new RuntimeException("Forced error...........");

	}
    
    
}