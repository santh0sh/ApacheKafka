package com.wecandonow;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import java.util.HashMap;
import java.util.Map;

public class Streams {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: <input_topic> <output_topic>");
			return;
		}
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-stream-processing-application");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		StreamsConfig config = new StreamsConfig(props);
		StreamsBuilder builder = new StreamsBuilder();
		builder.<String, String>stream(args[0]).mapValues(value -> value.toUpperCase() + "").to(args[1]);
		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.start();
	}
}