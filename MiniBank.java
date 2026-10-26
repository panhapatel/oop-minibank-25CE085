import java.util.Scanner;
import model.Customer;
import model.Account;
import model.WithdrawRule; 
import util.Validator;
import exception.InsufficientBalanceException;


import static java.lang.Math.max;

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

        System.out.println("\n--- Part B Testing: Interfaces & Interest ---");
        Account acc1 = new Account("Riya Patel", 10000);
        System.out.println(acc1);
        
     
        long bonus = max(500, 200);
        System.out.println("Calculated Bonus (using static import Math.max): ₹" + bonus);

        System.out.println("\n--- Part B Testing: WithdrawRule ---");
        
        
        WithdrawRule atmRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.getBalance() - amount >= 2000; 
            }
        };

        WithdrawRule onlineRule = (account, amount) -> account.getBalance() >= amount;

     
        System.out.println("ATM Withdrawal Allowed (req ₹5000): " + atmRule.allow(acc1, 5000));
        System.out.println("Online Withdrawal Allowed (req ₹9000): " + onlineRule.allow(acc1, 9000));

        System.out.println("\n--- Exception Handling Test ---");
        try {
            acc1.withdraw(15000); 
        } catch (InsufficientBalanceException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        MenuOption choice = null;

        do {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer\n5. Exit");
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