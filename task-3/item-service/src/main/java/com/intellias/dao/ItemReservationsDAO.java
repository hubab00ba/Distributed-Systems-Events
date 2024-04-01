package com.intellias.dao;

public interface ItemReservationsDAO {
	boolean doesReservationForOrderExists(String itemId, String orderId);
	void reserveItemForOrder(String itemId, String orderId);
	void removeReservationForOrder(String itemId, String orderId);
}
