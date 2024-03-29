package com.intellias.service.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.UserBalanceDAO;
import com.intellias.dao.UserTransactionsDAO;
import com.intellias.dto.PaymentResult;
import com.intellias.service.OrderPaymentService;

public class OrderPaymentServiceImpl implements OrderPaymentService {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaymentServiceImpl.class);

	private final UserTransactionsDAO userTransactionsDAO;
	private final UserBalanceDAO userBalanceDAO;
	private final TransactionTemplate transactionTemplate;

	public OrderPaymentServiceImpl(
			UserTransactionsDAO userTransactionsDAO,
			UserBalanceDAO userBalanceDAO,
			TransactionTemplate transactionTemplate
	) {
		this.userTransactionsDAO = userTransactionsDAO;
		this.userBalanceDAO = userBalanceDAO;
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	public PaymentResult payForOrder(String userId, String orderId, BigDecimal amount) {
		LOGGER.info("Paying for order {} user = {} amount = {}", orderId, userId, amount);
		if (userTransactionsDAO.doesTransactionExist(userId, orderId)) {
			LOGGER.info("Transaction already exists!");
			return PaymentResult.ALREADY_WITHDRAWN;
		}
		var currentBalance = userBalanceDAO.fetchBalanceOfUser(userId);
		if (amount.compareTo(currentBalance) > 0) {
			LOGGER.info("User doesn't have enough money. Current balance = {}", currentBalance);
			return PaymentResult.INSUFFICIENT_FUNDS;
		}
		transactionTemplate.executeWithoutResult(res -> {
			userBalanceDAO.updateUserBalance(userId, amount.negate());
			userTransactionsDAO.createTransaction(userId, orderId, amount);
		});
		LOGGER.info("Balance of user {} updated and transaction was created!", userId);
		return PaymentResult.MONEY_WITHDRAWN;
	}
}
