package com.finance.modulr.account;

import java.math.BigDecimal;

public interface AccountService {

    boolean withdrawAmount(int accountId, BigDecimal amount);

    String checkBalance(int accountId);
}
