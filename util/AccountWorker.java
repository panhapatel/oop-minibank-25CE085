package util;

import model.Account;
import exception.BankException;

public class AccountWorker implements Runnable {
    private Account account;
    private int times;
    private long amount;

    public AccountWorker(Account account, int times, long amount) {
        this.account = account;
        this.times = times;
        this.amount = amount;
    }

    @Override
    public void run() {
        System.out.println("Thread started: " + Thread.currentThread().getName());
        for (int i = 0; i < times; i++) {
            try {
                account.deposit(amount);
            } catch (BankException e) {
                System.out.println("Error in thread " + Thread.currentThread().getName() + ": " + e.getMessage());
            }
        }
        System.out.println("Thread finished: " + Thread.currentThread().getName());
    }
}
