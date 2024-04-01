package com.intellias.dto;

import java.math.BigDecimal;

public record ItemReservationResult(Status status, BigDecimal currentPrice) {
	public enum Status {
		ALREADY_RESERVED_FOR_ORDER,
		OUT_OF_STOCK,
		RESERVED
	}
}
