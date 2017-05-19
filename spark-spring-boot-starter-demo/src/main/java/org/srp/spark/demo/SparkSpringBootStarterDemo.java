package org.srp.spark.demo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import scala.Tuple2;

@SpringBootApplication
public class SparkSpringBootStarterDemo implements CommandLineRunner {

	@Autowired
	private JavaSparkContext sparkContext;

	@Autowired
	private SparkSession sparkSession;

	@Autowired
	private JavaStreamingContext streamingContext;

	public static void main(String[] args) {
		SpringApplication.run(SparkSpringBootStarterDemo.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {

		sparkContext.parallelize(Arrays.asList(1, 2, 3, 4, 5)).map(eachData -> eachData + 1).collect().stream()
				.forEach(System.out::println);

		sparkSession.read().json("src/main/resources/data.json").printSchema();

	    final Queue<JavaRDD<String>> queue = new LinkedList<JavaRDD<String>>();
	    queue.add(sparkContext.parallelize(Arrays.asList("1, India, Rupee, Delhi, NarendraModi, Indians, Bollywood", "2, USA, Dollar, WashingtonDC, DonaldTrump, Americans, Hollywood")));
	    final JavaDStream<String> stream = streamingContext.queueStream(queue);
	    stream.flatMap(line -> Arrays.asList(line.split(",")).iterator()).mapToPair(word -> new Tuple2<String, Integer>(word, 1)).reduceByKey((val1, val2) -> val1 + val2).print();
		streamingContext.start();
		streamingContext.awaitTermination();
	}

}
