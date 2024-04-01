package com.intellias.commands;

public record PurchaseCommand(String orderId, String userId, String itemId) {
}
