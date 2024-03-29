package com.intellias.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import com.intellias.dao.ItemDetailsDAO;
import com.intellias.dao.ItemReservationsDAO;

class TestItemReleaseServiceImpl {

	public static final String ITEM_ID = "1";
	public static final String ORDER_ID = "123";
	ItemReservationsDAO itemReservationsDAO = mock(ItemReservationsDAO.class);

	ItemDetailsDAO itemDetailsDAO = mock(ItemDetailsDAO.class);

	TransactionTemplate transactionTemplate = mock(TransactionTemplate.class);

	ItemReleaseServiceImpl itemReleaseService = new ItemReleaseServiceImpl(itemReservationsDAO, itemDetailsDAO, transactionTemplate);


	@Test
	void releaseItemForOrderGivenItemForOrderAlreadyReleased() {
		when(itemReservationsDAO.doesReservationForOrderExists(ITEM_ID, ORDER_ID)).thenReturn(false);
		itemReleaseService.releaseItemForOrder(ITEM_ID, ORDER_ID);
		verify(itemReservationsDAO).doesReservationForOrderExists(ITEM_ID, ORDER_ID);
		verifyNoMoreInteractions(itemReservationsDAO, itemDetailsDAO);
	}

	@SuppressWarnings("unchecked")
	@Test
	void releaseItemForOrderGivenItemForOrderNotReleasedYet() {
		when(itemReservationsDAO.doesReservationForOrderExists(ITEM_ID, ORDER_ID)).thenReturn(true);
		doAnswer(v -> {
			v.getArgument(0, Consumer.class).accept(null);
			return null;
		}).when(transactionTemplate).executeWithoutResult(any());
		itemReleaseService.releaseItemForOrder(ITEM_ID, ORDER_ID);
		verify(itemDetailsDAO).updateAvailableQuantity(ITEM_ID, 1);
		verify(itemReservationsDAO).removeReservationForOrder(ITEM_ID, ORDER_ID);
	}

}