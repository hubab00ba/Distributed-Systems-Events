package com.intellias.dto;

public enum OrderStatus {
	// Valid states
	WAITING_FOR_ITEM_RESERVATION,
	WAITING_FOR_PAYMENT,
	READY_FOR_DELIVERY,

	// Invalid states
	OUT_OF_STOCK,
	INSUFFICIENT_FUNDS;

	public boolean isFailed() {
		return this == OUT_OF_STOCK || this == INSUFFICIENT_FUNDS;
	}

}
