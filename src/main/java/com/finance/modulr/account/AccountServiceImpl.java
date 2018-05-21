package com.finance.modulr.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AccountServiceImpl implements AccountService {
    private static Logger logger = LogManager.getLogger(AccountServiceImpl.class);

    private Map<Integer, BigDecimal> accountBalance = new HashMap<>();

    public AccountServiceImpl() {
        accountBalance.put(1001, BigDecimal.valueOf(2738.59));
        accountBalance.put(1002, BigDecimal.valueOf(23.00));
        accountBalance.put(1003, BigDecimal.valueOf(0.00));
    }

    /**
     * @param accountId The account identifier
     * @param amount The value to be withdrawn
     * @return {@code boolean} true if transaction success
     * @throws IllegalArgumentException when the amount is below zero
     * @throws IllegalArgumentException when accountId is unknown
     */
    public boolean withdrawAmount(int accountId, BigDecimal amount) {

        if (amount.signum() == -1) {
            logger.warn("The amount to be withdrawn is less than zero: {}", amount);
            throw new IllegalArgumentException("The amount cannot be negative number.");
        }

        if (!accountBalance.containsKey(accountId)) {
            logger.error("Could not find the account with ID=", accountId);
            throw new IllegalArgumentException("Could not find the account with ID=" + accountId);
        }

        BigDecimal updatedBalance = accountBalance.get(accountId).subtract(amount);
        if (updatedBalance.signum() == -1) {
            logger.info("There are inadequate funds in account with Id={}, with amount={} and final funds={}", accountId, amount, accountBalance.get(accountId));
            return false;
        }
        accountBalance.put(accountId, updatedBalance);
        logger.info("Dispensing amount={} to accountId={}", amount, accountId);
        return true;
    }

    /**
     * @param accountId the account identifier
     * @return the account balance
     * @throws IllegalArgumentException when accountId is unknown
     */
    public String checkBalance(int accountId) {
        if (!accountBalance.containsKey(accountId)) {
            throw new IllegalArgumentException("Could not find the account with ID=" + accountId);
        }
        return accountBalance.get(accountId).toPlainString();
    }
}
