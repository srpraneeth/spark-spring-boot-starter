# spark-spring-boot-starter

1) Spring Boot Starter for Spark
2) Demo Project to use spark-spring-boot-starter

This creates following beans
1) SparkContext
2) SparkSession
3) StreamingContext


Properties
spark.appname=TestApp
spark.master=spark://localhost:3305
#spark.master defaults to local[2] if not provided
spark.props.driver.cores=1
spark.props.driver.maxResultSize=1g
spark.props.driver.memory=1g
spark.props.local.dir=/tmp
#Any spark conf property can be provided using spark.prop namespace
spark.streaming.duration=1000
#Streaming window duration
