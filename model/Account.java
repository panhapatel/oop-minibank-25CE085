package model;

import java.util.Objects;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;

public class Account implements Transactable, InterestBearing, Comparable<Account> {
    @Id
    private final String accountNumber;
    
    private String ownerName;
    
    @Positive(message = "must be > 0")
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
    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        balance += amount;
        System.out.println("Deposited ₹" + amount + ". New Balance: ₹" + balance);
    }

    @Override
    public boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        if (balance < amount) {
            long shortfall = amount - balance;
            throw new InsufficientFundsException("Insufficient funds for withdrawal.", shortfall);
        }
        balance -= amount;
        System.out.println("Withdrew ₹" + amount + ". Remaining Balance: ₹" + balance);
        return true;
    }

    public void transfer(Account targetAccount, long amount) throws BankException {
        System.out.println("Initiating transfer of ₹" + amount + " to " + targetAccount.getAccountNumber() + "...");
        try {
            this.withdraw(amount);
            targetAccount.deposit(amount);
            System.out.println("Transfer successful!");
        } catch (BankException e) {
            System.out.println("Transfer failed during process. Rolling back/handling...");
            throw new BankException("Transfer error: " + e.getMessage());
        } finally {
            System.out.println("Transfer transaction audit log completed.");
        }
    }

    @Override
    public double interestRate() {
        return 4.5;
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | Balance: ₹" + balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public long getBalance() { return balance; }
    public boolean isActive() { return active; }
}