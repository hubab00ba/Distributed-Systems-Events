package com.intellias.service;

import java.math.BigDecimal;

import com.intellias.dto.PaymentResult;

public interface OrderPaymentService {
	PaymentResult payForOrder(String userId, String orderId, BigDecimal amount);
}
