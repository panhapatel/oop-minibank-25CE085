package model;

import java.util.Objects;
import exception.InsufficientBalanceException;
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
    public void deposit(long amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited ₹" + amount + ". New Balance: ₹" + balance);
        } else {
            System.out.println("Invalid deposit amount!");
        }
    }

    @Override
    public boolean withdraw(long amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount!");
            return false;
        }
        if (balance < amount) {
            throw new InsufficientBalanceException("Withdrawal failed! Insufficient balance.");
        }
        balance -= amount;
        System.out.println("Withdrew ₹" + amount + ". Remaining Balance: ₹" + balance);
        return true;
    }

  
    @Override
    public double interestRate() {
        return 4.5;
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | Balance: ₹" + balance + " | Active: " + active;
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