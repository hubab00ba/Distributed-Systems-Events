package com.intellias.dao;

import java.math.BigDecimal;

public interface UserTransactionsDAO {
	boolean doesTransactionExist(String userId, String orderId);
	void createTransaction(String userId, String orderId, BigDecimal amount);
	BigDecimal getTransactionAmount(String userId, String orderId);
	void removeTransaction(String userId, String orderId);
}
