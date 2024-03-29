package com.intellias.event_handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intellias.dao.OrdersDAO;
import com.intellias.dto.OrderStatus;
import com.intellias.events.handling.EventHandler;
import com.intellias.events.types.users.WithdrawMoneyForOrderSucceeded;

public class MoneyForOrderWithdrawnEventHandler implements EventHandler<WithdrawMoneyForOrderSucceeded> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MoneyForOrderWithdrawnEventHandler.class);

	private final OrdersDAO ordersDAO;

	public MoneyForOrderWithdrawnEventHandler(OrdersDAO ordersDAO) {
		this.ordersDAO = ordersDAO;
	}

	@Override
	public void handleEvent(WithdrawMoneyForOrderSucceeded event) {
		var orderId = event.orderId();
		LOGGER.info("Money for order {} were withdrawn! Order is ready for delivery!", orderId);
		ordersDAO.changeStatusOfOrder(orderId, OrderStatus.READY_FOR_DELIVERY);
	}
}
