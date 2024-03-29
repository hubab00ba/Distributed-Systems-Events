package com.intellias.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.UserBalanceDAO;
import com.intellias.dao.UserTransactionsDAO;

class TestOrderDepositServiceImpl {

	public static final String USER_ID = "1";
	public static final String ORDER_ID = "2353";
	public static final BigDecimal TRANSACTION_AMOUNT = BigDecimal.TEN;
	UserTransactionsDAO userTransactionsDAO = mock(UserTransactionsDAO.class);
	UserBalanceDAO userBalanceDAO = mock(UserBalanceDAO.class);
	TransactionTemplate transactionTemplate = mock(TransactionTemplate.class);

	OrderDepositServiceImpl orderDepositService = new OrderDepositServiceImpl(userTransactionsDAO, userBalanceDAO, transactionTemplate);

	@Test
	void depositMoneyBackGivenTransactionWasAlreadyRemoved() {
		when(userTransactionsDAO.doesTransactionExist(USER_ID, ORDER_ID)).thenReturn(false);
		orderDepositService.depositMoneyBack(ORDER_ID, USER_ID);
		verifyNoInteractions(userBalanceDAO);
	}

	@SuppressWarnings("unchecked")
	@Test
	void depositMoneyBackGivenTransactionNotYetRemoved() {
		when(userTransactionsDAO.doesTransactionExist(USER_ID, ORDER_ID)).thenReturn(true);
		doAnswer(v -> {
			v.getArgument(0, Consumer.class).accept(null);
			return null;
		}).when(transactionTemplate).executeWithoutResult(any());
		when(userTransactionsDAO.getTransactionAmount(USER_ID, ORDER_ID)).thenReturn(TRANSACTION_AMOUNT);
		orderDepositService.depositMoneyBack(ORDER_ID, USER_ID);
		verify(userBalanceDAO).updateUserBalance(USER_ID, TRANSACTION_AMOUNT);
		verify(userTransactionsDAO).removeTransaction(USER_ID, ORDER_ID);
	}

}