package com.monitoring.kafka;

import kafka.common.OffsetAndMetadata;
import kafka.coordinator.group.GroupMetadataManager;
import kafka.coordinator.group.OffsetKey;
import scala.Option;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.log4j.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;

public class KafkaConsumerOffsetsParser {

	private static KafkaConsumer<byte[], byte[]> consumer;
	private static final Logger LOGGER = Logger.getLogger(KafkaConsumerOffsetsParser.class);

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "offset-consumer");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "Kafka_ConsumerOffsets_Monitor");
		props.put("exclude.internal.topics", "false");

		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("__consumer_offsets"));

		while (true) {
			try {
				ConsumerRecords<byte[], byte[]> consumerRecords = consumer.poll(100);
				if (consumerRecords.count() > 0) {
					consumerRecords.forEach(consumerRecord -> {
						byte[] key = consumerRecord.key();
						byte[] value;
						if (key != null) {
							Object o = GroupMetadataManager.readMessageKey(ByteBuffer.wrap(key));
							if (o != null && o instanceof OffsetKey) {
								OffsetKey offsetKey = (OffsetKey) o;
								value = consumerRecord.value();
								OffsetAndMetadata offsetAndMetadata = GroupMetadataManager
										.readOffsetMessageValue(ByteBuffer.wrap(value));
								// For print purpose
								Object groupTopicPartition = offsetKey.key();
								String formattedValue = String
										.valueOf(GroupMetadataManager.readOffsetMessageValue(ByteBuffer.wrap(value)));
								// For print purpose

								ConsumerOffsetDetails detail = new ConsumerOffsetDetails();
								detail.setTopic(offsetKey.key().topicPartition().topic());
								detail.setVersion(offsetKey.version());
								detail.setPartition(offsetKey.key().topicPartition().partition());
								detail.setOffset(offsetAndMetadata.offset());
								detail.setMetadata(offsetAndMetadata.metadata());
								detail.setGroup(offsetKey.key().group());
								detail.setExpireTimestamp(offsetAndMetadata.expireTimestamp().getOrElse(() -> 0L));
								detail.setCommitTimestamp(offsetAndMetadata.commitTimestamp());

								ObjectMapper om = new ObjectMapper();
								try {
									LOGGER.info(om.writeValueAsString(detail));
								} catch (JsonProcessingException e) {
									e.printStackTrace();
								}
							}
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			consumer.commitSync();
		}

	}
}
