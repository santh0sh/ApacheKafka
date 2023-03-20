package com.admatic;

import java.util.Properties;
import java.util.Scanner;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

public class CountryProducer {
	public static void main(String[] argv) {
		if (argv.length != 1) {
			System.err.println("Usage: <topic>");
			return;
		}
		String topicName = argv[0];
		Scanner in = new Scanner(System.in);
		System.out.println("Enter message: ");
		Properties configProperties = new Properties();
		configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
		configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
		configProperties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CountryPartitioner.class.getCanonicalName());
		configProperties.put("partitions.0", "India");
		configProperties.put("partitions.1", "US");
		KafkaProducer<String, String> producer = new KafkaProducer<>(configProperties);
		String line = in.nextLine();
		while (!line.equals("exit")) {
			ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, line);
			producer.send(producerRecord);
			line = in.nextLine();
		}
		in.close();
		producer.close();
	}
}