### Task: Implement your own Saga

#### Objective:

Implement Saga pattern. You are free to choose between Orchestration or Choreography Saga.

#### Requirements:

1. **Routes**:

   - POST /purchase

2. **Services**:

   - Users Service
   - Items Service
   - Orders Service

3. **Data Models**:

   User:

   ```json
   {
     "id": "id",
     "email": "string",
     "balance": "number"
   }
   ```

   Item:

   ```json
   {
     "id": "id",
     "price": "number",
     "quantity": "number"
   }
   ```

   Order:

   ```json
   {
     "id": "id",
     "user": "id",
     "item": "id"
   }
   ```

4. **Steps**:

   1. Update the item quantity and take the price
   2. Update the user balance
      - If fail then rollback the item quantity
   3. Create the order
      - If fail then rollback the user balance and item quantity

5. **Technology Stack**:

   - You are free to choose any technology stack for this task, but the provided solution utilizes
     Node.js, Express.js, Mongoose for MongoDB integration, and AMQP protocol with RabbitMQ
     for message queuing.
