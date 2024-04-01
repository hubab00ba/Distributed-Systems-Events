package com.intellias.dao;

import java.math.BigDecimal;

public interface UserBalanceDAO {
	BigDecimal fetchBalanceOfUser(String userId);
	void updateUserBalance(String userId, BigDecimal delta);
}
