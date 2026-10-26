import java.util.Scanner;

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
                    System.out.println("Invalid choice! Please enter a number between 1 and 5.");
                    continue;
                }

                String placeholderResponse = switch (choice) {
                    case OPEN_ACCOUNT -> "Open Account to be implemented in a later lab.";
                    case DEPOSIT -> "Deposit to be implemented in a later lab.";
                    case WITHDRAW -> "Withdraw to be implemented in a later lab.";
                    case TRANSFER -> "Transfer to be implemented in a later lab.";
                    case EXIT -> "Thank you for using MiniBank. Goodbye!";
                };

                System.out.println(">> " + placeholderResponse);

            } else {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.next(); 
            }

        } while (choice != MenuOption.EXIT);

        scanner.close();
    }
}