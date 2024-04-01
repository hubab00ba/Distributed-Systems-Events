package com.intellias.events.types.users;

import java.math.BigDecimal;

public record WithdrawMoneyForOrder(String userId, String orderId, BigDecimal amount) {
}
