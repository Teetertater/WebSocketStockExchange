# WebSocketStockExchange

### Intro
This is a basic Stock Exchange service enabling users to place orders and view a live-updating Top 10 list of buy/sell orders
Implemented with Kafka messaging queues for order persistence and WebSockets for handling client connections. Includes a simple user interface.

### Architecture:
![architecture image](architecture.png)

### Build and Run instructions:
1)  [Install Gradle](https://spring.io/guides/gs/gradle/#initial)  
2)  [Install Apache Kafka (Tested with version 2.5.0)](https://kafka.apache.org/quickstart)  
3)  Download all the files and cd into directory
4)  Run `gradlew build -x test` (gradle can be built with tests after running once, due to auto-creation of kafka topics)  
5)  Run `gradlew run`    
6)  Access client UI (default is [localhost:8081](http://localhost:8081) but can be changed in application.yml)
