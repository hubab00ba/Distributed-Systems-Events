package com.intellias.serialization;

public interface Deserializer {
	<T> T deserialize(byte[] bytes, Class<T> type);
}
