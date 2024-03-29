package com.intellias.events.types.items;

import java.math.BigDecimal;

public record ItemReservationSucceeded(String orderId, BigDecimal price) {
}
