package com.finance.modulr.account;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountServiceImplTest {

    private final AccountService accountService = new AccountServiceImpl();

    @Test
    void testCheckBalanceAccount1() {
        assertThat(accountService.checkBalance(1001)).isEqualTo("2738.59");
    }

    @Test
    void testCheckBalanceAccount3() {
        assertThat(accountService.checkBalance(1003)).isEqualTo("0.0");
    }

    @Test
    void withdrawAmount_throwsException_whenAccountIdIsNotFound() {
        int accountId = 1000;

        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> accountService.withdrawAmount(accountId, BigDecimal.valueOf(2)));

        assertThat(throwable.getMessage()).isEqualTo("Could not find the account with ID=1000");

    }

    @Test
    void withDrawAmount_subtractAmountFrom_balance() {
        int accountId = 1001;
        accountService.withdrawAmount(accountId, BigDecimal.valueOf(38.59));

        assertThat(accountService.checkBalance(accountId)).isEqualTo("2700.00");
    }

    @Test
    void withdrawAmount_withdrawsLessThanFundsAvailable() {
        int accountId = 1001;
        boolean returnValue = accountService.withdrawAmount(accountId, BigDecimal.valueOf(30));

        assertThat(returnValue).isTrue();
    }

    @Test
    void withdrawAmount_throwsException_whenAmountIsNegative() {
        int accountId = 1001;

        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> accountService.withdrawAmount(accountId, BigDecimal.valueOf(-1)));

        assertThat(throwable.getMessage()).isEqualTo("The amount cannot be negative number.");
    }

    @Test
    void checkBalance_throwsException_whenAccountIdIsNotFound() {
        int accountId = 1000;

        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> accountService.checkBalance(accountId));

        assertThat(throwable.getMessage()).isEqualTo("Could not find the account with ID=1000");
    }

    @Test
    void withdrawAmount_withdrawsMoreThanFundsAvailable() {
        int accountId = 1001;
        boolean returnValue = accountService.withdrawAmount(accountId, BigDecimal.valueOf(3000));

        assertThat(returnValue).isFalse();
    }
}
