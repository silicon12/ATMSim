package com.finance.modulr.atm;

import com.finance.modulr.account.AccountService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;


class ATMServiceImplTest {

    private ATMServiceImpl atmService;

    private AccountService accountServiceMock;

    @BeforeEach
    void setup() {
        accountServiceMock = Mockito.mock(AccountService.class);
        atmService = new ATMServiceImpl(accountServiceMock);
        atmService.replenishATM();
    }

    @Test
    void checkBalance() {
        int accountId = 1001;
        atmService.checkBalance(accountId);

        Mockito.verify(accountServiceMock, times(1)).checkBalance(accountId);
    }

    @Test
    void withDrawAmount_withLessThan20() {
        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> atmService.withdrawAmount(1001, 1));

        assertThat(throwable.getMessage()).isEqualTo("Cannot dispense amounts less than 20");
    }

    @Test
    void withDrawAmount_withMoreThan250() {
        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> atmService.withdrawAmount(1001, 251));

        assertThat(throwable.getMessage()).isEqualTo("Cannot withdraw more than 250");
    }

    @Test
    void withdrawAmount_ThrowsException_whenAccountService_throwsAnException() {

        int amount = 30;
        int accountId = 1001;
        doThrow(new IllegalArgumentException("Cannot withdraw")).when(accountServiceMock).
                withdrawAmount(accountId, BigDecimal.valueOf(amount));

        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> atmService.withdrawAmount(accountId, amount));

        assertThat(throwable.getMessage()).isEqualTo("Cannot withdraw");
    }

    @Test
    void withDrawAmount_returns_correct_notes_for_100() {
        atmService.getBankNotes().put(5, 1);
        atmService.getBankNotes().put(50, 2);
        Integer amount = 100;
        int accountId = 1001;

        given(accountServiceMock.withdrawAmount(accountId, BigDecimal.valueOf(amount))).willReturn(true);
        List<Integer> notes = atmService.withdrawAmount(accountId, amount);

        assertThat(notes).containsExactlyInAnyOrder(50, 50);
    }

    @Test
    void withDrawAmount_returns_correct_notes_for_50() {
        Integer amount = 50;
        int accountId = 1001;

        given(accountServiceMock.withdrawAmount(accountId, BigDecimal.valueOf(amount))).willReturn(true);
        List<Integer> notes = atmService.withdrawAmount(accountId, amount);

        assertThat(notes).containsExactlyInAnyOrder(5, 5, 20, 20);
    }

    @Test
    void withDrawAmount_returns_correct_notes_for_25() {
        Integer amount = 25;
        int accountId = 1001;

        given(accountServiceMock.withdrawAmount(accountId, BigDecimal.valueOf(amount))).willReturn(true);
        List<Integer> notes = atmService.withdrawAmount(accountId, amount);
        assertThat(notes).containsExactlyInAnyOrder(20, 5);
    }

    @Test
    void withDrawAmount_returns_correct_notes_for_30() {
        atmService.getBankNotes().put(5, 1);
        Integer amount = 30;
        int accountId = 1001;
        given(accountServiceMock.withdrawAmount(accountId, BigDecimal.valueOf(amount))).willReturn(true);

        List<Integer> notes = atmService.withdrawAmount(accountId, amount);
        assertThat(notes).containsExactlyInAnyOrder(20, 10);
    }

    @Test
    void withDrawAmount_returns_exception_for_23() {
        atmService.getBankNotes().put(5, 1);

        Integer amount = 23;
        int accountId = 1001;
        given(accountServiceMock.withdrawAmount(accountId, BigDecimal.valueOf(amount))).willReturn(true);

        Throwable throwable = assertThrows(IllegalArgumentException.class,
                () -> atmService.withdrawAmount(accountId, amount));

        assertThat(throwable.getMessage()).isEqualTo("Cannot dispense amounts that are not divisible by a factor of 5");
    }

    @Test
    void replenish_test() {
        atmService.getBankNotes().put(20, 0);
        atmService.getBankNotes().put(10, 0);
        atmService.getBankNotes().put(5, 0);

        Throwable throwable = assertThrows(IllegalStateException.class,
                () -> atmService.withdrawAmount(1001, 20));

        assertThat(throwable.getMessage()).isEqualTo("Not enough bank notes");
    }

}