package com.intellias.serialization.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class TestGsonDeserializer {

	GsonDeserializer gsonDeserializer = new GsonDeserializer(new Gson());

	@Test
	void deserializeHappyPath() {
		assertThat(gsonDeserializer.deserialize("{\"name\": \"Alex\", \"age\": 20 }".getBytes(StandardCharsets.UTF_8), User.class))
				.isEqualTo(new User("Alex", 20));
	}

	private record User(String name, int age) {
	}

}
