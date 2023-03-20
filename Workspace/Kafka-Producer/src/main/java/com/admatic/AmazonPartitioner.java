package com.admatic;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import java.util.HashMap;
import java.util.Map;

public class AmazonPartitioner implements Partitioner {
	private static Map<String, Integer> amazonPartitionMap;
// This method will gets called at the start, you should use  it to do 	one time	startup activity

	public void configure(Map<String, ?> configs) {
		amazonPartitionMap = new HashMap<>();
		for (Map.Entry<String, ?> entry : configs.entrySet()) {

			String key = entry.getKey();
			String entryValue = (String) entry.getValue();
			if (entry.getKey().startsWith("partitions.")) {
				int partitionId = Integer.parseInt(key.substring(11));
				amazonPartitionMap.put(entryValue, partitionId);
			}
			
		}
	}

//This method will get called once for each message
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		String orderKey = ((String) value).split(":")[0];
		if (orderKey.startsWith("P")) {
			System.out.println("Call made to Priority Mapping");
			return amazonPartitionMap.get("Prime");
		} else {
			int noOfPartitions = cluster.topics().size();
			return value.hashCode() % noOfPartitions + amazonPartitionMap.size();
		}
	}

	@Override
	public void close() {
	}
}