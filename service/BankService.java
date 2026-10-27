package service;

import model.Account;
import util.Repository;
import exception.BankException;
import java.util.Collection;

public class BankService {
    private final Repository<Account> repository;

    public BankService(Repository<Account> repository) {
        this.repository = repository;
    }

    public Account openAccount(String ownerName, long initialBalance) throws BankException {
        if (initialBalance < 0) {
            throw new BankException("Initial balance cannot be negative.");
        }
        Account account = new Account(ownerName, initialBalance);
        repository.save(account);
        return account;
    }

    public void deposit(String accountNumber, long amount) throws BankException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new BankException("Account not found: " + accountNumber);
        }
        account.deposit(amount);
    }

    public void withdraw(String accountNumber, long amount) throws BankException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new BankException("Account not found: " + accountNumber);
        }
        account.withdraw(amount);
    }

    public void transfer(String srcNum, String tgtNum, long amount) throws BankException {
        Account src = repository.findById(srcNum);
        Account tgt = repository.findById(tgtNum);
        if (src == null || tgt == null) {
            throw new BankException("Source or target account not found.");
        }
        src.transfer(tgt, amount);
    }

    public Collection<Account> listAccounts() {
        return repository.findAll();
    }
}
