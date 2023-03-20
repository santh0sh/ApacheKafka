package com.admatic;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DLQ implements DeserializationExceptionHandler {
	private KafkaProducer<byte[], byte[]> dlqProducer;

	@Override
	public DeserializationHandlerResponse handle(final ProcessorContext context,
			final ConsumerRecord<byte[], byte[]> record, final Exception exception) {
		System.out.println("Exception caught during Deserialization, sending to the dead queue topic; " + "taskId: {"
				+ context.taskId() + "}, " + "topic: {" + record.topic() + "}, " + "partition: {" + record.partition()
				+ "}, " + "offset: {" + record.offset() + "}\n\n" + exception);
		String dlqTopic = "quarantine";
		dlqProducer.send(new ProducerRecord<>(dlqTopic, record.key(), record.value()));
		return DeserializationHandlerResponse.CONTINUE;
	}

	@Override
	public void configure(final Map<String, ?> configs) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("max.request.size", "99999999");
		props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		dlqProducer = new KafkaProducer<>(props);
	}

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: <input_topic> <output_topic>");
			return;
		}
		Map<String, Object> props = new HashMap<>();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-stream-processing-application");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, "com.admatic.DLQ");
		StreamsConfig config = new StreamsConfig(props);
		StreamsBuilder builder = new StreamsBuilder();
		builder.<Integer, String>stream(args[0]).mapValues(value -> value.length() + "").to(args[1]);
		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.start();
	}
}
