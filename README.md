# Voidium Market

*Voidium Market* is an event sourced and event driven marketplace application for the fictitious natural resource "voidium".
It has the following features:

* REST API for placing purchase orders and sell orders, as well as retrieving exists orders and transactions
* Broker service for brokering transaction between matching orders
* Simulators for simulating buyers and sellers placing orders
* Web UI for viewing price information, transactions, orders and all events recorded in the system

## Getting started

1. Download the Git repo:

```
git clone https://github.com/santosleijon/voidium-market.git
```

2. Start dependencies using Docker Compose:

```
docker-compose up
```

This will start the following services:

* [Zookeeper](https://github.com/apache/zookeeper) on port 2181
* [Apache Kafka](https://github.com/apache/kafka) on port 9092
* [UI for Apache Kafka](https://github.com/provectus/kafka-ui) on port 9093 (http://localhost:9093/)
* [PostgreSQL](https://www.postgresql.org/) on port 5432

3. Build and start the Java application:

```
mvn clean install
java -jar ./target/voidium-market-0.0.1-SNAPSHOT.jar
```

4. View the simulated orders and transactions being created through the admin UI: http://localhost:8080

5. Use the REST API to create, retrieve or update additional data:

| HTTP Method | Path                  | Description                                                              |
|-------------|-----------------------|--------------------------------------------------------------------------|
| GET         | /purchase-orders/{id} | Retrieve all purchase orders, excluding their associated transactions.   |
| GET         | /purchase-orders/{id} | Retrieve a single purchase order, including its associated transactions. |
| POST        | /purchase-orders/     | Place a new purchase order.                                              |
| DELETE      | /purchase-orders/{id} | Delete a purchase order.                                                 |
| GET         | /sale-orders/{id}     | Retrieve all sale orders, excluding their associated transactions.       |
| GET         | /sale-orders/{id}     | Retrieve a single sale order, including its associated transactions.     |
| POST        | /sale-orders/         | Place a new sale order.                                                  |
| DELETE      | /sale-orders/{id}     | Delete a sale order.                                                     |

## Technology stack

### Backend
  * [Java 21](https://dev.java/) - The most recent LTS release of Java at the time of writing.
  * [Spring Boot](https://github.com/spring-projects/spring-boot) - Industry standard framework for creating production-grade stand-alone Java applications.
  * [Apache Kafka](https://kafka.apache.org/) - Open-source distributed event streaming platform used as an event bus and event store.
  * [JUnit Jupiter](https://junit.org/junit5/) - Testing framework used to write and execute unit tests and integration tests (part of JUnit 5).

### Frontend
  * [Thymeleaf](https://www.thymeleaf.org/) - Java template engine used to generate web pages.
  * [Pure CSS](https://purecss.io/) - Small and minimalist CSS framework used to creating a responsive layout, menu, tables and other components.
  * [Google Charts](https://developers.google.com/chart) - JavaScript visualization library used to generate [candlestick charts](https://en.wikipedia.org/wiki/Candlestick_chart) with pricing information.

## Architecture and design patterns

This application uses [event sourcing](https://www.eventstore.com/event-sourcing) as a way to store application state. It also utilizes [projections](https://www.eventstore.com/event-sourcing#Projections) to improve read and query performance.
The events that constitutes the application state are sent through Apache Kafka to allow processing by different parts of the application in an asynchronous way.

For example, when a purchase order is placed a `PurchaseOrderPlaced` event is stored in the event store and published to Kafka. The `PurchaseOrderProjector` listens to the Kafka event and updates the projections database with the new purchase order. Another event listener triggers the `BrokerService` which finds matching sale orders for the new purchase order and brokers any possible transaction between them.

![Event sourcing with projections and Apache Kafka](https://github.com/santosleijon/voidium-market/blob/main/docs/event-sourcing-with-projections.png?raw=true)
