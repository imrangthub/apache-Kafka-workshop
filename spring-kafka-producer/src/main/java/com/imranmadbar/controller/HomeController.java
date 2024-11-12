package com.imranmadbar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.imranmadbar.MessageProducer;

@RestController
public class HomeController {

	Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private MessageProducer messageProducer;

	@GetMapping("/send")
	public String sendMessage(@RequestParam("msg") String msg) {
		messageProducer.sendMessage("topic_sms_notification", msg);
		return "Message sent: " + msg;
	}

	@GetMapping(value = "/")
	public String welcomeMsg() {
		logger.info("Welcome to SpringBootKafkaApplication");
		return "Welcome to SpringBootKafkaApplication";
	}

	@GetMapping(value = "/home")
	public String helloMsg() {
		logger.info("Welcome Home to Spring Boot  SpringBootKafkaApplication");
		return "Welcome Home to Spring Boot  SpringBootKafkaApplication";
	}

}
