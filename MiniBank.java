import java.util.Scanner;
import java.nio.file.Path;
import model.Account;
import exception.BankException;
import util.Repository;
import util.StatePersister;
import util.TransactionLog;
import util.ReportGenerator;
import service.BankService;

record BankInfo(String name, String branch) {}

enum MenuOption {
    OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, LIST_ACCOUNTS, GENERATE_REPORT, EXIT
}

public class MiniBank {
    public static void main(String[] args) {
        BankInfo bankInfo = new BankInfo("MiniBank Central", "Ahmedabad Branch");
        System.out.println("==========================================");
        System.out.println("Welcome to " + bankInfo.name() + " (" + bankInfo.branch() + ")");
        System.out.println("==========================================");

        Path stateFile = Path.of("bank_state.dat");
        Path logsDir = Path.of("logs");
        Path reportFile = Path.of("eod_report.txt");

        Repository<Account> repository = new Repository<>();

        if (java.nio.file.Files.exists(stateFile)) {
            try {
                Account[] loaded = StatePersister.load(stateFile);
                repository.saveAll(loaded);
                System.out.println(">> Successfully loaded previous account states from file.");
            } catch (Exception e) {
                System.out.println(">> Load error: " + e.getMessage());
            }
        }

        BankService bankService = new BankService(repository);
        Scanner scanner = new Scanner(System.in);
        MenuOption choice = null;

        do {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. List All Accounts");
            System.out.println("6. Generate EOD Report");
            System.out.println("7. Exit");
            System.out.print("\nEnter your choice (1-7): ");

            if (scanner.hasNextInt()) {
                int optionNum = scanner.nextInt();
                scanner.nextLine();
                
                choice = switch (optionNum) {
                    case 1 -> MenuOption.OPEN_ACCOUNT;
                    case 2 -> MenuOption.DEPOSIT;
                    case 3 -> MenuOption.WITHDRAW;
                    case 4 -> MenuOption.TRANSFER;
                    case 5 -> MenuOption.LIST_ACCOUNTS;
                    case 6 -> MenuOption.GENERATE_REPORT;
                    case 7 -> MenuOption.EXIT;
                    default -> null;
                };

                if (choice == null) {
                    System.out.println("Invalid choice! Please enter 1-7.");
                    continue;
                }

                try {
                    switch (choice) {
                        case OPEN_ACCOUNT -> {
                            System.out.print("Enter Owner Name: ");
                            String name = scanner.nextLine();
                            System.out.print("Enter Initial Deposit (₹): ");
                            long amount = scanner.nextLong();
                            scanner.nextLine();

                            Account acc = bankService.openAccount(name, amount);
                            System.out.println(">> Success! Created Account: " + acc);
                        }
                        case DEPOSIT -> {
                            System.out.print("Enter Account Number: ");
                            String accNum = scanner.nextLine();
                            System.out.print("Enter Deposit Amount (₹): ");
                            long amount = scanner.nextLong();
                            scanner.nextLine();

                            bankService.deposit(accNum, amount);
                            TransactionLog.append(logsDir.resolve("tx_day1.log"), "DEPOSIT: ₹" + amount + " to " + accNum);
                            System.out.println(">> Deposit successful!");
                        }
                        case WITHDRAW -> {
                            System.out.print("Enter Account Number: ");
                            String accNum = scanner.nextLine();
                            System.out.print("Enter Withdrawal Amount (₹): ");
                            long amount = scanner.nextLong();
                            scanner.nextLine();

                            bankService.withdraw(accNum, amount);
                            TransactionLog.append(logsDir.resolve("tx_day1.log"), "WITHDRAW: ₹" + amount + " from " + accNum);
                            System.out.println(">> Withdrawal successful!");
                        }
                        case TRANSFER -> {
                            System.out.print("Enter Source Account Number: ");
                            String src = scanner.nextLine();
                            System.out.print("Enter Target Account Number: ");
                            String tgt = scanner.nextLine();
                            System.out.print("Enter Transfer Amount (₹): ");
                            long amount = scanner.nextLong();
                            scanner.nextLine();

                            bankService.transfer(src, tgt, amount);
                            TransactionLog.append(logsDir.resolve("tx_day1.log"), "TRANSFER: ₹" + amount + " from " + src + " to " + tgt);
                            System.out.println(">> Transfer successful!");
                        }
                        case LIST_ACCOUNTS -> {
                            System.out.println("\n--- All Accounts ---");
                            for (Account acc : bankService.listAccounts()) {
                                System.out.println(" -> " + acc);
                            }
                        }
                        case GENERATE_REPORT -> {
                            ReportGenerator.generateReport(logsDir, reportFile);
                        }
                        case EXIT -> {
                          
                            var allAccs = bankService.listAccounts().toArray(new Account[0]);
                            StatePersister.save(allAccs, stateFile);
                            System.out.println(">> Accounts saved securely. Thank you for using MiniBank!");
                        }
                    }
                } catch (Exception e) {
                    System.out.println(">> Error: " + e.getMessage());
                }

            } else {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();
            }
        } while (choice != MenuOption.EXIT);

        scanner.close();
    }
}