package com.intellias.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.ItemDetailsDAO;
import com.intellias.dao.ItemReservationsDAO;
import com.intellias.dto.ItemDetails;
import com.intellias.dto.ItemReservationResult;

class TestItemReservationsServiceImpl {
	private static final String ITEM_ID = "1";
	private static final String ORDER_ID = "123";
	private static final BigDecimal PRICE = BigDecimal.TEN;

	ItemDetailsDAO itemDetailsDAO = mock(ItemDetailsDAO.class);
	ItemReservationsDAO itemReservationsDAO = mock(ItemReservationsDAO.class);
	TransactionTemplate transactionTemplate = mock(TransactionTemplate.class);

	ItemReservationsServiceImpl itemReservationsService = new ItemReservationsServiceImpl(
			itemReservationsDAO,
			itemDetailsDAO,
			transactionTemplate
	);

	@Test
	void reserveItemForOrderGivenReservationAlreadyExists() {
		when(itemDetailsDAO.getItemDetails(ITEM_ID)).thenReturn(new ItemDetails(PRICE, 0));
		when(itemReservationsDAO.doesReservationForOrderExists(ITEM_ID, ORDER_ID)).thenReturn(true);
		assertThat(itemReservationsService.reserveItemForOrder(ITEM_ID, ORDER_ID))
				.isEqualTo(new ItemReservationResult(ItemReservationResult.Status.ALREADY_RESERVED_FOR_ORDER, PRICE));
	}


	@Test
	void reserveItemForOrderGivenReservationNotExistAndItemOutOfStock() {
		when(itemDetailsDAO.getItemDetails(ITEM_ID)).thenReturn(new ItemDetails(PRICE, 0));
		when(itemReservationsDAO.doesReservationForOrderExists(ITEM_ID, ORDER_ID)).thenReturn(false);
		assertThat(itemReservationsService.reserveItemForOrder(ITEM_ID, ORDER_ID))
				.isEqualTo(new ItemReservationResult(ItemReservationResult.Status.OUT_OF_STOCK, PRICE));
	}

	@SuppressWarnings("unchecked")
	@Test
	void reserveItemForOrderGivenReservationNotExistAndItemNotOutOfStock() {
		when(itemDetailsDAO.getItemDetails(ITEM_ID)).thenReturn(new ItemDetails(PRICE, 1));
		when(itemReservationsDAO.doesReservationForOrderExists(ITEM_ID, ORDER_ID)).thenReturn(false);
		doAnswer(v -> {
			v.getArgument(0, Consumer.class).accept(null);
			return null;
		}).when(transactionTemplate).executeWithoutResult(any());
		assertThat(itemReservationsService.reserveItemForOrder(ITEM_ID, ORDER_ID))
				.isEqualTo(new ItemReservationResult(ItemReservationResult.Status.RESERVED, PRICE));
		verify(itemDetailsDAO).updateAvailableQuantity(ITEM_ID, -1);
		verify(itemReservationsDAO).reserveItemForOrder(ITEM_ID, ORDER_ID);
	}

}
