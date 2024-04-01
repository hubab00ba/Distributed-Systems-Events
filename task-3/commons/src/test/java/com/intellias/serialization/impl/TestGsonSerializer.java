package com.intellias.serialization.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class TestGsonSerializer {

	GsonSerializer gsonSerializer = new GsonSerializer(new Gson());

	@Test
	void serialize() {
		assertThat(gsonSerializer.serialize(new User("William")))
				.isEqualTo("{\"name\":\"William\"}".getBytes(StandardCharsets.UTF_8));
	}

	private record User(String name) {
	}

}