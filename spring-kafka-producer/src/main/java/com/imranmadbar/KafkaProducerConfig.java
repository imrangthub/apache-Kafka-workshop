package com.imranmadbar;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {
	
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	
	
	@Bean
	public ProducerFactory<String, String> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.16.8.132:9092");
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}
	
	
//    @Value("${big-data-kafka.consuming-trigger-list:T09,T31,T12,T37}")
//    private List<String> consumingTgList;
//
//    @Value("${big-data-kafka.bootstrap-servers:192.168.1.1:9092}")
//    private List<String> bigDataKafkaBootServers;
//
//    @Bean
//    public ConsumerFactory<String, BgdKafkaRecord> bgdKafkaConsumerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bigDataKafkaBootServers);
//        return new DefaultKafkaConsumerFactory<>(
//                props,
//                new StringDeserializer(),
//                new JsonDeserializer<>(BgdKafkaRecord.class)
//        );
//    }
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, BgdKafkaRecord> bgdKafkaListener() {
//        ConcurrentKafkaListenerContainerFactory<String, BgdKafkaRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(bgdKafkaConsumerFactory());
//        factory.setRecordFilterStrategy(record -> !consumingTgList.contains(record.value().getTriggerId()));
//        return factory;
//    }
    
    


}