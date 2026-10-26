import java.util.Scanner;
import model.Account;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import util.BankResource;
import util.AccountWorker;

record BankInfo(String name, String branch) {}

enum MenuOption {
    OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
}

public class MiniBank {
    public static void main(String[] args) {
        BankInfo bankInfo = new BankInfo("MiniBank Central", "Ahmedabad Branch");
        System.out.println("==========================================");
        System.out.println("Welcome to " + bankInfo.name() + " (" + bankInfo.branch() + ")");
        System.out.println("==========================================");

       
        System.out.println("\n--- Testing: Try-with-resources ---");
        try (BankResource resource = new BankResource()) {
            resource.processSession();
        } catch (Exception e) {
            System.out.println("Resource error: " + e.getMessage());
        }

        System.out.println("\n--- Testing: Exception Hierarchy & Shortfall ---");
        Account acc1 = new Account("Riya Patel", 5000);
        Account acc2 = new Account("Amit Shah", 2000);

        System.out.println(acc1);
        System.out.println(acc2);

        try {
            acc1.withdraw(8000);
        } catch (InsufficientFundsException e) {
            System.out.println("Caught Insufficient Funds: " + e.getMessage());
            System.out.println("Shortfall amount: ₹" + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Caught Invalid Amount: " + e.getMessage());
        } finally {
            System.out.println("Withdrawal transaction attempt finished.");
        }

        try {
            acc1.transfer(acc2, 1000);
        } catch (BankException e) {
            System.out.println("Transfer error caught: " + e.getMessage());
        }

       
        System.out.println("\n--- Testing: Multi-threading & Synchronization ---");
        Account sharedAccount = new Account("Shared User", 1000);
        
        int numberOfThreads = 10;
        int timesPerThread = 100;
        long depositAmount = 10;
        
        Thread[] threads = new Thread[numberOfThreads];
        
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new AccountWorker(sharedAccount, timesPerThread, depositAmount), "Worker-" + (i + 1));
            threads[i].start();
        }
        
      
        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }
        
        System.out.println("Expected Final Balance: ₹" + (1000 + (numberOfThreads * timesPerThread * depositAmount)));
        System.out.println("Actual Final Balance: ₹" + sharedAccount.getBalance());

      
        Scanner scanner = new Scanner(System.in);
        MenuOption choice = null;

        do {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("\nEnter your choice (1-5): ");

            if (scanner.hasNextInt()) {
                int optionNum = scanner.nextInt();
                
                choice = switch (optionNum) {
                    case 1 -> MenuOption.OPEN_ACCOUNT;
                    case 2 -> MenuOption.DEPOSIT;
                    case 3 -> MenuOption.WITHDRAW;
                    case 4 -> MenuOption.TRANSFER;
                    case 5 -> MenuOption.EXIT;
                    default -> null;
                };

                if (choice == null) {
                    System.out.println("Invalid choice! Please enter 1-5.");
                    continue;
                }

                String msg = switch (choice) {
                    case OPEN_ACCOUNT -> "Open Account feature.";
                    case DEPOSIT -> "Deposit feature.";
                    case WITHDRAW -> "Withdraw feature.";
                    case TRANSFER -> "Transfer feature.";
                    case EXIT -> "Thank you for using MiniBank!";
                };
                System.out.println(">> " + msg);

            } else {
                System.out.println("Invalid input!");
                scanner.next();
            }
        } while (choice != MenuOption.EXIT);

        scanner.close();
    }
}