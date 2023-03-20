package com.admatic;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;

public class ProducerWithKey {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: <topic>");
			return;
		}
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		KafkaProducer<String, String> producer = new KafkaProducer<>(props);
		
		producer.send(new ProducerRecord<>(args[0], "Key","I am a value"));
		
		producer.send(new ProducerRecord<>(args[0], "TN","Tirunelvelli"));
		producer.send(new ProducerRecord<>(args[0], "TN","Chennai"));
		producer.send(new ProducerRecord<>(args[0], "TN","Madurai"));
		
		producer.send(new ProducerRecord<>(args[0], "KA","Mysore"));
		producer.send(new ProducerRecord<>(args[0], "KA","Banglore"));
		producer.send(new ProducerRecord<>(args[0], "KA","Manglore"));
		
		producer.send(new ProducerRecord<>(args[0], "KL","Allepey"));
		producer.send(new ProducerRecord<>(args[0], "KL","Guruvayoor"));
		producer.send(new ProducerRecord<>(args[0], "KL","Pallakkad"));
		producer.close();
	}
}