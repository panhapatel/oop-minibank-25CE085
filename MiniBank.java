import java.util.Scanner;
import model.Account;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import util.BankResource;

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
            System.out.println("Withdrawal transaction attempt finished (finally block executed).");
        }

      
        System.out.println("\n--- Testing: Transfer with try-catch-finally ---");
        try {
            acc1.transfer(acc2, 1000);
        } catch (BankException e) {
            System.out.println("Transfer error caught: " + e.getMessage());
        }

        System.out.println("\nMilestone 1 checkpoint completed successfully!");

       
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