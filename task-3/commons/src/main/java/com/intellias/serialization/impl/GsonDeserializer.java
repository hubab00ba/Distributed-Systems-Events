package com.intellias.serialization.impl;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.intellias.serialization.Deserializer;

public class GsonDeserializer implements Deserializer {
	private final Gson gson;

	public GsonDeserializer(Gson gson) {
		this.gson = gson;
	}

	@Override
	public <T> T deserialize(byte[] bytes, Class<T> type) {
		return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), type);
	}
}
