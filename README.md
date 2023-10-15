# apache-Kafka-workshop

##SetUp Kafka SingleCluster
-------------------------------------------------

  Start Zookeeper:
  
        =>./zookeeper-server-start.sh ../config/zookeeper.properties

  Add the below properties in the server.properties
  
          listeners=PLAINTEXT://localhost:9092
          auto.create.topics.enable=false


   Start Kafka Broker:
   
        =>./kafka-server-start.sh ../config/server.properties


  Create a topic:
  
        =>./kafka-topics.sh --create --topic test-topic --bootstrap-server localhost:9092 --partitions 4 --replication-factor 1
        =>./kafka-topics.sh --list  --bootstrap-server localhost:9092


        =>./kafka-topics.sh --bootstrap-server localhost:9092 --describe
        =>./kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic test-topic



  Create Console Producer:
  
        =>./kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic
        =>./kafka-console-producer.sh --broker-list localhost:9092 --topic test-topic --property "key.separator=-" --property "parse.key=true"

  Create Console Consumer:
  
        =>./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning
        =>./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test-topic --from-beginning -property "key.separator= - " --property "print.key=true"




  Consumer Groups:
  
        =>./kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
