package com.intellias.serialization.impl;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.intellias.serialization.Serializer;

public class GsonSerializer implements Serializer {
	private final Gson gson;

	public GsonSerializer(Gson gson) {
		this.gson = gson;
	}

	@Override
	public byte[] serialize(Object obj) {
		return gson.toJson(obj).getBytes(StandardCharsets.UTF_8);
	}
}
