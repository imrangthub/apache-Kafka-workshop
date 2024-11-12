package com.imranmadbar;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SimpleListener {

	@KafkaListener(topics = "test-topic0", groupId = "my-consumer-group")
	public void listen(String message) {
		System.out.println("ReceivedMsg: " + message);
	}


}