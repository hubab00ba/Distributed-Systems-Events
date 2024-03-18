### Task: Implement and Stress Test a User Data Management Application

#### Objective:

Create 2 (or more) services with their own databases and hit the inconsistency state between them during data duplication.

#### Requirements:

1. **Routes**:

   - POST service-1 - create entry in the database

   - GET service-2?id - returns the duplicated entry from service-1

2. **Data Model Example**:

   ```json
   {
     "username": "string",
     "email": "string",
     "age": "number"
   }
   ```

3. **Technology Stack**:

   - You are free to choose any technology stack for this task, but the provided solution utilizes
     Node.js, Express.js, Mongoose for MongoDB integration, and AMQP protocol with RabbitMQ
     for message queuing.

4. **Stress Testing**:

   - You will write a test to simulate high traffic, sending multiple create requests to Service 1
     and immediately reading from Service 2. The test should identify at least one instance where
     the data created by Service 1 is not yet available in Service 2, demonstrating the inconsistency.

#### Submission:

Submit your completed project, including all source code, and a report on your stress testing findings.
