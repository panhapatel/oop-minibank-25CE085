import java.util.Scanner;
import java.nio.file.Path;
import java.util.ArrayList;
import model.Account;
import exception.BankException;
import util.TransactionProcessor;
import util.TransactionBuffer;
import util.StatePersister;
import util.TransactionLog;
import util.ReportGenerator;
import util.AccountStore;
import util.Repository;

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

        System.out.println("\n--- Testing: Generic Repository & Reflection ---");
        Repository<Account> accountRepo = new Repository<>();
        
        Account rAcc1 = new Account("Kiran", 4000);
        Account rAcc2 = new Account("Neha", 8500);
        
        accountRepo.saveAll(rAcc1, rAcc2);

        String searchId = rAcc1.getAccountNumber();
        Account fetched = accountRepo.findById(searchId);
        System.out.println("Fetched by ID (" + searchId + "): " + fetched);

        System.out.println("All accounts in Repository:");
        for (Account acc : accountRepo.findAll()) {
            System.out.println(" -> " + acc);
        }

        Double[] balances = {4000.0, 8500.0, 1500.0};
        System.out.println("Average Balance via generic method: ₹" + Repository.average(balances));

        Scanner scanner = new Scanner(System.in);
        AccountStore store = new AccountStore();
        store.add(rAcc1);
        store.add(rAcc2);

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
                scanner.nextLine();
                
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

                try {
                    switch (choice) {
                        case OPEN_ACCOUNT -> {
                            System.out.print("Enter Owner Name: ");
                            String name = scanner.nextLine();
                            System.out.print("Enter Initial Deposit Amount (₹): ");
                            long amount = scanner.nextLong();
                            scanner.nextLine();

                            Account newAcc = new Account(name, amount);
                            store.add(newAcc);
                            accountRepo.save(newAcc);
                            System.out.println(">> Success! Account Created & Saved to Repository: " + newAcc);
                        }
                        case DEPOSIT -> {
                            System.out.print("Enter Account Number (e.g., AC0001): ");
                            String accNum = scanner.nextLine();
                            Account target = store.get(accNum);
                            if (target != null) {
                                System.out.print("Enter Deposit Amount (₹): ");
                                long amt = scanner.nextLong();
                                scanner.nextLine();
                                target.deposit(amt);
                                System.out.println(">> Deposited successfully! New Balance: ₹" + target.getBalance());
                            } else {
                                System.out.println(">> Error: Account not found!");
                            }
                        }
                        case WITHDRAW -> {
                            System.out.print("Enter Account Number (e.g., AC0001): ");
                            String accNum = scanner.nextLine();
                            Account target = store.get(accNum);
                            if (target != null) {
                                System.out.print("Enter Withdrawal Amount (₹): ");
                                long amt = scanner.nextLong();
                                scanner.nextLine();
                                target.withdraw(amt);
                                System.out.println(">> Withdrawn successfully! Remaining Balance: ₹" + target.getBalance());
                            } else {
                                System.out.println(">> Error: Account not found!");
                            }
                        }
                        case TRANSFER -> {
                            System.out.print("Enter Source Account Number: ");
                            String srcNum = scanner.nextLine();
                            System.out.print("Enter Target Account Number: ");
                            String tgtNum = scanner.nextLine();
                            
                            Account srcAcc = store.get(srcNum);
                            Account tgtAcc = store.get(tgtNum);

                            if (srcAcc != null && tgtAcc != null) {
                                System.out.print("Enter Transfer Amount (₹): ");
                                long amt = scanner.nextLong();
                                scanner.nextLine();
                                srcAcc.transfer(tgtAcc, amt);
                                System.out.println(">> Transfer completed successfully!");
                            } else {
                                System.out.println(">> Error: One or both accounts not found!");
                            }
                        }
                        case EXIT -> {
                            System.out.println(">> Thank you for using MiniBank. Goodbye!");
                        }
                    }
                } catch (Exception e) {
                    System.out.println(">> Transaction Error: " + e.getMessage());
                }

            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();
            }
        } while (choice != MenuOption.EXIT);

        scanner.close();
    }
}