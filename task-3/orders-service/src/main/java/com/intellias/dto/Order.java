package com.intellias.dto;

public record Order(String userId, String itemId, OrderStatus orderStatus) {
}
