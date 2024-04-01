package com.intellias.dao;

import com.intellias.dto.OrderStatus;

public interface OrdersDAO {
	void initOrder(String orderId, String itemId, String userId);
	String getOrderCreator(String orderId);
	String getItemIdByOrderId(String orderId);
	void changeStatusOfOrder(String orderId, OrderStatus newStatus);
}
