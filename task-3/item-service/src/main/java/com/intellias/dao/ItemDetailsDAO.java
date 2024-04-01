package com.intellias.dao;

import com.intellias.dto.ItemDetails;

public interface ItemDetailsDAO {
	ItemDetails getItemDetails(String itemId);
	void updateAvailableQuantity(String itemId, int quantityDelta);
}
