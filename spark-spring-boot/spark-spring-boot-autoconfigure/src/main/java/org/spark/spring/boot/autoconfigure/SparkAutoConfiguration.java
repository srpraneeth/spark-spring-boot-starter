package org.spark.spring.boot.autoconfigure;

import java.util.Optional;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.spark.spring.boot.autoconfigure.properties.SparkProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ SparkConf.class, JavaSparkContext.class })
@EnableConfigurationProperties(SparkProperties.class)
public class SparkAutoConfiguration {

	@Autowired
	private SparkProperties sparkProperties;

	@Bean
	@ConditionalOnMissingBean
	public SparkConf sparkConf() {
		Optional.ofNullable(sparkProperties.getAppname()).orElseThrow(() -> new IllegalArgumentException("Spark Conf Bean not created. App Name not defined."));
		final SparkConf conf = new SparkConf();
		sparkProperties.getProps().forEach((prop, value) -> conf.set("spark." + (String)prop, (String)value)); 
		return conf.setAppName(sparkProperties.getAppname()).setMaster(Optional.ofNullable(sparkProperties.getMaster()).map(master -> master).orElse("local"));
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(SparkConf.class)
	public JavaSparkContext sparkContext() {
		return new JavaSparkContext(sparkConf());
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(JavaStreamingContext.class)
	@ConditionalOnBean(JavaSparkContext.class)
	@ConditionalOnProperty(name="spark.streaming.duration")
	public JavaStreamingContext streamingContext(){
		return new JavaStreamingContext(sparkContext(), new Duration(sparkProperties.getStreaming().getDuration()));
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnClass(SparkSession.class)
	@ConditionalOnProperty(name="spark.appname")
	public SparkSession sqlSession(){
		Optional.ofNullable(sparkProperties.getAppname()).orElseThrow(() -> new IllegalArgumentException("Spark Session Bean not created. App Name not defined."));
		return SparkSession.builder().appName(sparkProperties.getAppname()).config(sparkConf()).getOrCreate();
	}
	
}
