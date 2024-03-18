const mongoose = require("mongoose");
const express = require("express");
const Data = require("./data");
const amqplib = require("amqplib");

(async () => {
  await mongoose
    .connect("mongodb://localhost/task-2-read")
    .then(() => console.log("Connected to MongoDB..."))
    .catch((err) => console.error("Could not connect to MongoDB...", err));

  const conn = await amqplib.connect("amqp://localhost");

  const channel = await conn.createChannel();
  await channel.assertQueue("data-queue");

  await channel.consume(
    "data-queue",
    async (msg) => {
      if (msg !== null) {
        const data = JSON.parse(msg.content.toString());
        await Data.create(data);
      }
    },
    { noAck: true },
  );

  const app = express();

  app.get("/", async (req, res) => {
    const data = await Data.findOne({ _id: req.query._id });

    return res.send(data);
  });

  app.listen(3001, () => console.log(`Listening on port 3001 ...`));
})();
