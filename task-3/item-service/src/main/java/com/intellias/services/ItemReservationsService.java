package com.intellias.services;

import com.intellias.dto.ItemReservationResult;

public interface ItemReservationsService {
	ItemReservationResult reserveItemForOrder(String itemId, String orderId);
}
