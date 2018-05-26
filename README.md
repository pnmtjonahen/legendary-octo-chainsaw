# Resto
Chez, Philippe

Resto, Chez Philippe, Is a virtual restaurant where you can order food and drinks. The restaurant is a showcase of all kinds of different technologies.

## The Parts
The resto consists of a number of different projects with each its own speciality.

### frontdesk
This is a plain javascript frontend that communicates with the diner microservice.

It uses the diner to get the menu, place orders, get order status updates and pay the bill.

### diner
This microservices is implements the services that the frontend needs. It uses the chef and bartender microservices to do the actual work of preparing the food and mixing the drink.

### chef
The chef microservice is responsible for preparing the food. 

### bartender
The bartender microservice is responsible for mixing drinks.

### Technologies
- REST endpoints: All services for getting the menu and posting new orders by the frontdesk are REST endpoints.
- Diner -> Bartender and Diner -> Chef for getting the menu are done with REST endpoints.
- WebSocket: frontdesk opens a websocket to get status updates on submitted order. Diner sends (pushes) status updates to the frontdesk via this websocket.
- AMQP (rabbitmq): When submitting new drink order to the bartender AMQP is used. 
- @Async methods
- Config server
- Zipkin server / sleuth for tracing
- Eureka Server / Eureka client / Ribbon @LoadBalanced RestTemplates
- JPA repository for database access
