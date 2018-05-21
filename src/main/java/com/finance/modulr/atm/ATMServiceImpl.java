package com.finance.modulr.atm;

import com.finance.modulr.account.AccountService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;

public class ATMServiceImpl {

    private  static Logger logger = LogManager.getLogger(ATMServiceImpl.class);

    private final AccountService accountService;
    private static final Integer MIN_DISPENSE_AMOUNT = 20;
    private static final Integer MAX_DISPENSE_AMOUNT = 250;
    private final Map<Integer, Integer> bankNotes = new HashMap<>();

    public ATMServiceImpl(AccountService accountService) {

        this.accountService = accountService;
    }

    public Map<Integer, Integer> getBankNotes() {

        return bankNotes;
    }

    public String checkBalance(int accountId) {

        return accountService.checkBalance(accountId);
    }

    /**
     * @param accountId the account identifier
     * @param amount the amount to be withdrawn
     * @return @{code List}of {@code Integer}(bank notes)
     * @throws IllegalArgumentException when the amount is less than 20
     * @throws IllegalArgumentException when the amount is more than 250
     * @throws IllegalArgumentException when the amount is not dividable by 5
     */
    public List<Integer> withdrawAmount(int accountId, Integer amount) {

        checkAmountBounds(amount);

        List<Integer> bankNotes = getBankNotes(amount);
        accountService.withdrawAmount(accountId, BigDecimal.valueOf(amount));
        logger.info("Dispensing amount={} from account with ID={}", amount, accountId);
        return bankNotes;
    }

    private void checkAmountBounds(Integer amount) {
        if (amount < MIN_DISPENSE_AMOUNT) {
            logger.warn("Cannot dispense amounts less than 20");
            throw new IllegalArgumentException("Cannot dispense amounts less than 20");
        }
        if (amount > MAX_DISPENSE_AMOUNT) {
            logger.warn("Cannot dispense amounts greater than 250");
            throw new IllegalArgumentException("Cannot withdraw more than 250");
        }
        if (amount % 5 != 0) {
            logger.warn("Cannot dispense amounts that are not divisible by a factor of 5");
            throw new IllegalArgumentException("Cannot dispense amounts that are not divisible by a factor of 5");
        }
    }

    private List<Integer> getBankNotes(Integer amount) {
        List<Integer> solveWithFive = attemptWithFive(amount, new HashMap<>(bankNotes));
        if (!solveWithFive.isEmpty()) {
            updateBank(solveWithFive);
            return solveWithFive;
        }

        logger.warn("Could not dispense amount with 5 bank note in it");

        List<Integer> solutionWithout5 = bankNotesRecursion(amount, new ArrayList<>(), new HashMap<>(bankNotes));
        if (!solutionWithout5.isEmpty()) {
            updateBank(solutionWithout5);
            return solutionWithout5;
        } else {
            logger.error("Could not dispense amount={}, because there are insufficient bank notes at present", amount);
            throw new IllegalStateException("Not enough bank notes");
        }
    }

    private void updateBank(List<Integer> notes) {
        for (int note : notes) {
            bankNotes.put(note, bankNotes.get(note) - 1);
        }
    }

    private List<Integer> attemptWithFive(Integer amount, Map<Integer, Integer> bankNotesCopy) {
        List<Integer> notes = new ArrayList<>();
        subtractNote(bankNotesCopy, notes, 5);
        return bankNotesRecursion(amount - 5, notes, bankNotesCopy);
    }

    private List<Integer> bankNotesRecursion(Integer amount, List<Integer> notes, Map<Integer, Integer> bankNotesCopy) {
        for (int note : descSort(bankNotes.keySet())) {
            if (amount >= note && bankNotesCopy.get(note) > 0) {
                subtractNote(bankNotesCopy, notes, note);
                return bankNotesRecursion(amount - note, notes, bankNotesCopy);
            }
        }
        return amount == 0 ? notes : new ArrayList<>();
    }

    private void subtractNote(Map<Integer, Integer> bankNotesCopy, List<Integer> notes, int note) {
        bankNotesCopy.put(note, bankNotesCopy.get(note) - 1);
        notes.add(note);
    }

    private List<Integer> descSort(Set<Integer> keysSet) {
        List<Integer> keys = new ArrayList<>(keysSet);
        keys.sort(Comparator.reverseOrder());
        return keys;
    }

    public void replenishATM() {
        bankNotes.put(5, 20);
        bankNotes.put(10, 20);
        bankNotes.put(20, 20);
        bankNotes.put(50, 20);
    }
}
