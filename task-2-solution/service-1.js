const express = require("express");
const amqplib = require("amqplib");
const NodeCache = require("node-cache");

(async () => {
  const conn = await amqplib.connect("amqp://localhost");
  const channel = await conn.createChannel();
  await channel.assertQueue("data-queue");

  const cache = new NodeCache({ stdTTL: 0 });

  const app = express();

  await channel.consume(
    "data-queue",
    async (msg) => {
      if (msg !== null) {
        const data = JSON.parse(msg.content.toString());
        cache.set(data._id, data);
      }
    },
    { noAck: true },
  );

  app.get("/", async (req, res) => {
    const data = cache.get(req.query._id);
    return res.send(data);
  });

  app.listen(3001, () => console.log(`Listening on port 3001 ...`));
})();
