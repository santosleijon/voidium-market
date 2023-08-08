#!/bin/bash

topics=("purchase-orders" "sale-orders" "transactions")

for topic in "${topics[@]}"
do
  docker-compose exec kafka kafka-topics \
    --create \
    --topic "$topic" \
    --partitions 1 \
    --replication-factor 1 \
    --if-not-exists \
    --bootstrap-server localhost:9092
done
