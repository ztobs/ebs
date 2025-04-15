#!/bin/bash

# Wait for Kafka to be ready
while ! nc -z kafka 9092; do
  echo "Waiting for Kafka..."
  sleep 1
done

# Booking Service Topics
kafka-topics --bootstrap-server kafka:9092 --create --topic booking.event.created --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --topic booking.event.cancelled --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --topic booking.event.updated --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --topic booking.command.validate --partitions 3 --replication-factor 1

# Inventory Service Topics
kafka-topics --bootstrap-server kafka:9092 --create --topic inventory.event.updated --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --topic inventory.command.reserve --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --topic inventory.command.release --partitions 3 --replication-factor 1

# Notification Service Topics
kafka-topics --bootstrap-server kafka:9092 --create --topic notification.event.sent --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server kafka:9092 --create --topic notification.command.send --partitions 3 --replication-factor 1

echo "Kafka topics created successfully"