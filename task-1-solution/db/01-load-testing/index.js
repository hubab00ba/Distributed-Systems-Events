import Fastify from "fastify";
import pkg from "pg";
import { faker } from "@faker-js/faker";

const { Pool } = pkg;
const pool = new Pool({
  user: "testUser",
  password: "123456",
  database: "loadTesting",
});

const fastify = Fastify({
  logger: true,
});

fastify.get("/", async function (request, reply) {
  const cookies = parseCookies(request);

  if (!cookies.userId) {
    reply.code(401);
    reply.send("User not found");
  }

  const res = await pool.query(`SELECT * FROM users WHERE id = $1`, [
    cookies.userId,
  ]);

  reply.send(getFulfilledTemplate(res.rows[0]));
});
fastify.post("/", async function (request, reply) {
  const id = Math.random().toString(36).substring(7);
  const res = await pool.query(
    `INSERT INTO users (id, username, email, age, avatar, birthdate, "registeredAt") VALUES ($1, $2, $3, $4, $5, $6, $7) RETURNING id`,
    [
      id,
      faker.person.fullName(),
      faker.internet.email(),
      faker.number.int({
        min: 18,
        max: 70,
      }),
      faker.internet.url(),
      faker.date.past({
        years: 70,
      }),
      new Date(),
    ]
  );

  reply.header("Set-Cookie", `userId=${id}`);
  reply.send(res.rows[0]);
});

fastify.get("/hello", function (request, reply) {
  reply.send({ hello: "world" });
});

fastify.listen({ port: 3000 }, function (err, address) {
  if (err) {
    fastify.log.error(err);
    process.exit(1);
  }
});

function getFulfilledTemplate(data) {
  return `
  <html>
    <head>
      <title>Load testing</title>
    </head>
    <body>
      <h1>Load testing</h1>
      <p>Request fulfilled</p>
      <pre>${JSON.stringify(data, null, 2)}</pre>
    </body>
  `;
}

function parseCookies(request) {
  const list = {};
  const cookieHeader = request.headers?.cookie;
  if (!cookieHeader) return list;

  cookieHeader.split(`;`).forEach(function (cookie) {
    let [name, ...rest] = cookie.split(`=`);
    name = name?.trim();
    if (!name) return;
    const value = rest.join(`=`).trim();
    if (!value) return;
    list[name] = decodeURIComponent(value);
  });

  return list;
}
