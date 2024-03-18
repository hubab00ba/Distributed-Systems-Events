const mongoose = require("mongoose");

const dataSchema = new mongoose.Schema(
  {
    username: String,
    email: String,
    age: Number,
  },
  { timestamps: true },
);

module.exports = mongoose.model("Data", dataSchema);
