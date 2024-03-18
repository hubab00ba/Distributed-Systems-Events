const mongoose = require("mongoose");
const express = require("express");
const Data = require("./data");
const { faker } = require("@faker-js/faker");
const amqplib = require("amqplib");

(async () => {
  await mongoose
    .connect("mongodb://localhost/task-2-write")
    .then(() => console.log("Connected to MongoDB..."))
    .catch((err) => console.error("Could not connect to MongoDB...", err));

  const conn = await amqplib.connect("amqp://localhost");

  const channel = await conn.createChannel();
  await channel.assertQueue("data-queue");

  const app = express();

  app.post("/", async (req, res) => {
    const data = await Data.create({
      username: faker.internet.userName(),
      email: faker.internet.email(),
      age: faker.number.int(),
    });

    channel.sendToQueue(
      "data-queue",
      Buffer.from(JSON.stringify(data.toJSON())),
    );

    return res.send(data);
  });

  app.listen(3000, () => console.log(`Listening on port 3000 ...`));
})();
