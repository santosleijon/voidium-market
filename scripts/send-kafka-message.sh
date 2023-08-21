#!/bin/bash

topic="SaleOrder"
message='{"id":"2e1aed1f-8348-4e0d-84ec-644c9c4946ca","type":"SaleOrderPlaced","date":1691695279.266634300,"aggregateName":"SaleOrder","aggregateId":"2e1aed1f-8348-4e0d-84ec-644c9c4946ca","unitsCount":1,"pricePerUnit":16.57,"validTo":1691695339.266634300}'

docker-compose exec kafka bash -c "echo '${message}' | kafka-console-producer --topic ${topic} --broker-list localhost:9092"
