### Task: Implement and Stress Test a User Data Management Application

#### Objective:

Create simple web application and stress test it.

#### Requirements:

1. **Routes**:

    - GET / - returns HTML which is populated with some static data

    - POST / - creates data entry in the database and returns HTML which displays it

2. **Data Model Example**:

   ```json
   {
      "username": "string",
      "email": "string",
      "age": "number",
      "avatar": "string",
      "password": "string",
      "birthdate": "date",
      "registeredAt": "date"
   }
   ```

3. **Technology Stack**:

    - You are free to choose any programming language, web framework, database,
      and templating engine. The provided solution uses Express, Mongoose (MongoDB), and Pug,
      but you are not required to use the same technologies.

4. **Stress Testing**:

    - After implementing your application, use a stress testing tool like Artillery or JMeter
      to simulate multiple concurrent users making requests to your application. Start with a low
      number of users and gradually increase the load to observe how your application's performance changes.

    - Analyze the test results to identify the maximum number of requests your application can
      handle before experiencing significant performance degradation or failures.

#### Submission:

Submit your completed project, including all source code, and a report on your stress testing findings.

Your report should have:
1. Details about your machine like CPU and RAM
2. Results of the stress testing
