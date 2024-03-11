const express = require("express");
const mongoose = require("mongoose");
const { faker } = require("@faker-js/faker");

(async () => {
  await mongoose
    .connect("mongodb://localhost/task-1")
    .then(() => console.log("Connected to MongoDB..."))
    .catch((err) => console.error("Could not connect to MongoDB...", err));

  const dataSchema = new mongoose.Schema(
    {
      username: String,
      email: String,
      avatar: String,
      password: String,
      birthdate: Date,
      registeredAt: Date,
    },
    { timestamps: true },
  );

  const Data = mongoose.model("Data", dataSchema);

  const app = express();

  app.set("view engine", "pug");
  app.set("views", "./views");

  app.use(express.urlencoded({ extended: true }));

  app.get("/", async (req, res) => {
    return res.render("index", {
      data: [
        {
          username: "Some Name",
          email: "some-mail@gmail.com",
          avatar: "test.jpg",
          password: "kBh0quSnTK0RZeX",
          birthdate: "1990-03-18T18:42:14.722Z",
          registeredAt: "2023-11-20T05:44:02.473Z",
        },
      ],
    });
  });

  app.post("/", async (req, res) => {
    const data = await Data.create({
      username: faker.internet.userName(),
      email: faker.internet.email(),
      avatar: faker.image.avatar(),
      password: faker.internet.password(),
      birthdate: faker.date.birthdate(),
      registeredAt: faker.date.past(),
    });

    return res.render("index", { data: [data] });
  });

  app.listen(3000, () => console.log(`Listening on port 3000 ...`));
})();
