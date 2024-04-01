package com.intellias.dto;

public record EventEnvelope(String eventType, Object payload) {
}
