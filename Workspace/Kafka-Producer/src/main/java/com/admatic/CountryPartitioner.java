package com.admatic;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import java.util.HashMap;
import java.util.Map;

public class CountryPartitioner implements Partitioner {
	private static Map<String, Integer> countryToPartitionMap;
// This method will gets called at the start, you should use  it to do 	one time	startup activity

	public void configure(Map<String, ?> configs) {
		countryToPartitionMap = new HashMap<>();
		for (Map.Entry<String, ?> entry : configs.entrySet()) {
			if (entry.getKey().startsWith("partitions.")) {
				String key = entry.getKey();
				String value = (String) entry.getValue();
				int partitionId = Integer.parseInt(key.substring(11));
				countryToPartitionMap.put(value, partitionId);
			}
		}
	}

//This method will get called once for each message
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		String countryName = ((String) value).split(":")[0];
		if (countryToPartitionMap.containsKey(countryName)) {
//If the country is mapped to particular partition return it
			return countryToPartitionMap.get(countryName);
		} else {
//If no country is mapped to particular partition distribute between remaining partitions
			int noOfPartitions = cluster.topics().size();
			return value.hashCode() % noOfPartitions + countryToPartitionMap.size();
		}
	}

	@Override
	public void close() {
	}
}