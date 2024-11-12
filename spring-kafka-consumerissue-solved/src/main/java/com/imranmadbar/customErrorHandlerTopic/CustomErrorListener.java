package com.imranmadbar.customErrorHandlerTopic;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.imranmadbar.Book;

@Component
public class CustomErrorListener {

	@KafkaListener(topics = "test-topic2", groupId = "my-consumer-group", containerFactory = "kafkaListenerContainerFactory2")
	public void listen(Book obj) {
		System.out.println("ReceivedBookObj: " + obj);
	}

}
