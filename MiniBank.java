import java.util.Scanner;
import java.nio.file.Path;
import model.Account;
import exception.BankException;
import util.BankResource;
import util.TransactionProcessor;
import util.TransactionBuffer;
import util.StatePersister;
import util.TransactionLog;
import util.ReportGenerator;

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

        System.out.println("\n--- Testing: Transaction Processor (Thread Pool) ---");
        TransactionProcessor processor = new TransactionProcessor(4);
        Account testAcc = new Account("Priya Shah", 1000);

        for (int i = 1; i <= 5; i++) {
            final int taskNo = i;
            processor.submit(() -> {
                try {
                    testAcc.deposit(100);
                    System.out.println("Task " + taskNo + " completed by " + Thread.currentThread().getName());
                } catch (BankException e) {
                    System.out.println("Task error: " + e.getMessage());
                }
            });
        }
        processor.stop();
        System.out.println("Final Balance after pool tasks: ₹" + testAcc.getBalance());

        System.out.println("\n--- Testing: Producer-Consumer Buffer ---");
        TransactionBuffer buffer = new TransactionBuffer();

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    final int currentI = i;
                    Runnable tx = () -> System.out.println("Executing buffered transaction #" + currentI);
                    buffer.produce(tx);
                    System.out.println("Produced transaction #" + currentI);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer-Thread");

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    Runnable tx = buffer.consume();
                    tx.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer-Thread");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- Testing: Deadlock-Free Transfers ---");
        Account accA = new Account("Account A", 5000);
        Account accB = new Account("Account B", 5000);

        Thread t1 = new Thread(() -> {
            try {
                accA.transfer(accB, 500);
            } catch (BankException e) {
                System.out.println("Transfer A->B error: " + e.getMessage());
            }
        }, "Transfer-Thread-1");

        Thread t2 = new Thread(() -> {
            try {
                accB.transfer(accA, 300);
            } catch (BankException e) {
                System.out.println("Transfer B->A error: " + e.getMessage());
            }
        }, "Transfer-Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final Acc A Balance: ₹" + accA.getBalance());
        System.out.println("Final Acc B Balance: ₹" + accB.getBalance());


        System.out.println("\n--- Testing: Persistence & Reporting ---");
        Path stateFile = Path.of("bank_state.dat");
        Path logsDir = Path.of("logs");
        Path reportFile = Path.of("eod_report.txt");

   
        Account[] accountsToSave = { accA, accB };
        try {
            StatePersister.save(accountsToSave, stateFile);
        } catch (Exception e) {
            System.out.println("Save error: " + e.getMessage());
        }

   
        try {
            TransactionLog.append(logsDir.resolve("tx_day1.log"), "DEPOSIT: ₹500 to AC0001");
            TransactionLog.append(logsDir.resolve("tx_day1.log"), "WITHDRAW: ₹200 from AC0002");
        } catch (Exception e) {
            System.out.println("Log error: " + e.getMessage());
        }

        try {
            Account[] reloadedAccounts = StatePersister.load(stateFile);
            System.out.println("Reloaded Accounts from file:");
            for (Account acc : reloadedAccounts) {
                System.out.println(" -> " + acc);
            }
        } catch (Exception e) {
            System.out.println("Load error: " + e.getMessage());
        }

        try {
            ReportGenerator.generateReport(logsDir, reportFile);
        } catch (Exception e) {
            System.out.println("Report error: " + e.getMessage());
        }

        System.out.println("\n--- All Automated Tests & Reporting Completed! ---");

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