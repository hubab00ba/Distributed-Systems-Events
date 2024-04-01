package com.intellias.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.UserBalanceDAO;
import com.intellias.dao.UserTransactionsDAO;
import com.intellias.dto.PaymentResult;

class TestOrderPaymentServiceImpl {
	private static final String USER_ID = "123";
	private static final String ORDER_ID = "235235";
	private static final BigDecimal AMOUNT = BigDecimal.TEN;
	private static final BigDecimal DELTA = BigDecimal.valueOf(2);

	UserTransactionsDAO userTransactionsDAO = mock(UserTransactionsDAO.class);
	UserBalanceDAO userBalanceDAO = mock(UserBalanceDAO.class);
	TransactionTemplate transactionTemplate = mock(TransactionTemplate.class);

	OrderPaymentServiceImpl orderPaymentService = new OrderPaymentServiceImpl(userTransactionsDAO, userBalanceDAO, transactionTemplate);

	@Test
	void payForOrderGivenTransactionAlreadyExists() {
		when(userTransactionsDAO.doesTransactionExist(USER_ID, ORDER_ID)).thenReturn(true);
		assertThat(orderPaymentService.payForOrder(USER_ID, ORDER_ID, AMOUNT)).isEqualTo(PaymentResult.ALREADY_WITHDRAWN);
	}

	@Test
	void payForOrderGivenUserDoesntHaveEnoughMoney() {
		when(userTransactionsDAO.doesTransactionExist(USER_ID, ORDER_ID)).thenReturn(false);
		when(userBalanceDAO.fetchBalanceOfUser(USER_ID)).thenReturn(AMOUNT.subtract(DELTA));
		assertThat(orderPaymentService.payForOrder(USER_ID, ORDER_ID, AMOUNT)).isEqualTo(PaymentResult.INSUFFICIENT_FUNDS);
	}

	@SuppressWarnings("unchecked")
	@Test
	void payForOrderGivenUserHasEnoughMoneyForOrder() {
		when(userTransactionsDAO.doesTransactionExist(USER_ID, ORDER_ID)).thenReturn(false);
		when(userBalanceDAO.fetchBalanceOfUser(USER_ID)).thenReturn(AMOUNT.add(DELTA));
		doAnswer(v -> {
			v.getArgument(0, Consumer.class).accept(null);
			return null;
		}).when(transactionTemplate).executeWithoutResult(any());
		assertThat(orderPaymentService.payForOrder(USER_ID, ORDER_ID, AMOUNT)).isEqualTo(PaymentResult.MONEY_WITHDRAWN);
		verify(userBalanceDAO).updateUserBalance(USER_ID, AMOUNT.negate());
		verify(userTransactionsDAO).createTransaction(USER_ID, ORDER_ID, AMOUNT);
	}

}