package com.intellias.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.UserBalanceDAO;
import com.intellias.dao.UserTransactionsDAO;
import com.intellias.service.OrderDepositService;

public class OrderDepositServiceImpl implements OrderDepositService {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderDepositServiceImpl.class);

	private final UserTransactionsDAO userTransactionsDAO;
	private final UserBalanceDAO userBalanceDAO;
	private final TransactionTemplate transactionTemplate;

	public OrderDepositServiceImpl(
			UserTransactionsDAO userTransactionsDAO,
			UserBalanceDAO userBalanceDAO,
			TransactionTemplate transactionTemplate
	) {
		this.userTransactionsDAO = userTransactionsDAO;
		this.userBalanceDAO = userBalanceDAO;
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	public void depositMoneyBack(String orderId, String userId) {
		LOGGER.info("Depositing money back to user {} for order = {}", userId, orderId);
		if (userTransactionsDAO.doesTransactionExist(userId, orderId)) {
			var transactionAmount = userTransactionsDAO.getTransactionAmount(userId, orderId);
			LOGGER.info("Returning amount = {} to user {}", transactionAmount, userId);
			transactionTemplate.executeWithoutResult(v -> {
				userTransactionsDAO.removeTransaction(userId, orderId);
				userBalanceDAO.updateUserBalance(userId, transactionAmount);
			});
			LOGGER.info("Operation was executed!");
		} else {
			LOGGER.info("Transaction was already removed...");
		}
	}
}
