package model;

import java.io.Serializable;
import java.util.Objects;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import exception.BankException;

public class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    private static long accountCounter = 0;

    private static synchronized String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        if (balance < amount) {
            long shortfall = amount - balance;
            throw new InsufficientFundsException("Insufficient funds for withdrawal.", shortfall);
        }
        balance -= amount;
        return true;
    }


    public void transfer(Account target, long amount) throws BankException {
        if (target == null) {
            throw new BankException("Target account cannot be null.");
        }
        if (this.accountNumber.equals(target.accountNumber)) {
            throw new BankException("Cannot transfer money to the same account.");
        }

        Account firstLock = this.accountNumber.compareTo(target.accountNumber) < 0 ? this : target;
        Account secondLock = firstLock == this ? target : this;

        synchronized (firstLock) {
            synchronized (secondLock) {
                this.withdraw(amount);
                target.deposit(amount);
            }
        }
    }

    @Override
    public double interestRate() {
        return 4.5;
    }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | Balance: ₹" + balance;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public long getBalance() { return balance; }
    public boolean isActive() { return active; }
}