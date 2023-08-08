#!/bin/bash

topic="test-topic"

docker-compose exec kafka kafka-topics --create --topic "$topic" --partitions 1 --replication-factor 1 --if-not-exists --bootstrap-server localhost:9092
docker-compose exec kafka bash -c "echo 'Hello, Kafka!' | kafka-console-producer --topic ${topic} --broker-list localhost:9092"
docker-compose exec kafka kafka-console-consumer --topic "$topic" --bootstrap-server localhost:9092 --from-beginning
