package model;

public class Account {
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
        this.balance = openingBalance > 0 ? openingBalance : 0;
        this.active = true;
    }

    
    public Account(String ownerName) {
        this(ownerName, 0);
    }

    
    public void deposit(long amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited " + amount + ". New Balance: " + balance);
        } else {
            System.out.println("Invalid deposit amount!");
        }
    }

    
    public boolean withdraw(long amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            System.out.println("Withdrew " + amount + ". Remaining Balance: " + balance);
            return true;
        } else {
            System.out.println("Withdrawal failed! Insufficient funds or invalid amount.");
            return false;
        }
    }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public long getBalance() { return balance; }
    public boolean isActive() { return active; }
}
