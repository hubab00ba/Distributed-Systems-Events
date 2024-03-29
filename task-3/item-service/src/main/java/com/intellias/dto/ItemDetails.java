package com.intellias.dto;

import java.math.BigDecimal;

public record ItemDetails(BigDecimal price, int quantity) {
	public boolean isOutOfStock() {
		return quantity == 0;
	}
}
