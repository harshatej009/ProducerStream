package harsh.rane;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KafkaMain2 {
	private static final Logger LOGGER = LogManager.getLogger(KafkaMain2.class);
	public static void main(String[] args) {
		Properties streamsConfiguration = new Properties();
		streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "KafkaMain");
		streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
	
		
		
		StreamsBuilder builder = new StreamsBuilder();  // building Kafka Streams Model
		
		KStream<Long, String> left = builder.stream("topic2");
	    KStream<Long, String> right = builder.stream("topic3");	
	    long joinWindowSizeMs = 60000; // 1 min
	    KGroupedStream<Object, String> wordCounts = left.flatMapValues(value -> Arrays.asList(value.toLowerCase())).groupBy((key,word) -> word);
	  //  KStream<Long, String> wordCounts1= left.join(right, (leftValue, rightValue) -> "left=" + leftValue + ", right=" + rightValue,  JoinWindows.of(joinWindowSizeMs));
	  ((KStream<Long, String>) wordCounts).foreach((key, value) -> {
			  System.out.println(key + " => " + value);
			  LOGGER.info(key + " => " + value);
			  });
	  left.foreach((key, value) -> {
		  System.out.println(key + " => " + value);
		  LOGGER.info(key + " => " + value);
		  });
	  
	    	//text2.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
      		//  .groupBy((key, word) -> word)
      		// .count();
	    
	   // wordCounts1.to("topicresult1");
	    ((KStream<Long, String>) wordCounts).to("topicresult");
	   
        
        KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration); //Starting kafka stream
        streams.start();
	}

}
