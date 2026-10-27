package util;

import model.Account;
import java.util.*;

public class AccountStore {
    private final Map<String, Account> accountsMap = new HashMap<>();
    private final List<String> transactionHistory = new ArrayList<>();

    public void add(Account account) {
        accountsMap.put(account.getAccountNumber(), account);
        transactionHistory.add("Added account: " + account.getAccountNumber());
    }

    public Account get(String accountNumber) {
        return accountsMap.get(accountNumber);
    }

    public Collection<Account> getAll() {
        return accountsMap.values();
    }

    public List<Account> sortedByBalance() {
        List<Account> list = new ArrayList<>(accountsMap.values());
        list.sort(Comparator.comparingLong(Account::getBalance));
        return list;
    }

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }
}
