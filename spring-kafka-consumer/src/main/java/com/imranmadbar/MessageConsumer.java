package com.imranmadbar;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

	@KafkaListener(topics = "test-topic1", groupId = "my-consumer-group")
	public void listen(String message) {
		System.out.println("ReceivedMsg: " + message);
	}
	
	
	
	
	
	
	
//	
//	@KafkaListener(topics = "my-topic", groupId = "my-group-id")
//	public void listen2(String message) {
//		System.out.println("Received message: " + message);
//	}

	
	

}