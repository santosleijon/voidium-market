# Voidium Market

*Voidium Market* is a web application for a marketplace for the fictitious natural resource "voidium".

## Technology stack

* Backend
  * [Java 17](https://dev.java/) - The most recent LTS release of the JDK supporting modern APIs such as records, pattern matching and concise switch expressions.
  * [Spring Boot](https://github.com/spring-projects/spring-boot) - Industry standard framework for creating production-grade stand-alone Java applications.
  * [Apache Kafka](https://kafka.apache.org/) - Open-source distributed event streaming platform used as an event bus and event store.
  * [JUnit Jupiter](https://junit.org/junit5/) - Testing framework used to write and execute unit tests and integration tests (part of JUnit 5).

## Architecture and design patterns

* Backend
  * [Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html) - The application state is stored as a sequence of immutable domain events, rather than a snapshot that is repeatedly overwritten.
  * [Repository pattern](https://martinfowler.com/eaaCatalog/repository.html) - Repository classes are used to create, update and retrieve domain entities without exposing data access logic.