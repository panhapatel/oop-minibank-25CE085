package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class ReportGenerator {
    public static void generateReport(Path logsDir, Path reportFile) throws IOException {
        long totalDeposits = 0;
        long totalWithdrawals = 0;

        System.out.println("\n--- Reading Transaction Logs ---");
        if (Files.exists(logsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, "*.log")) {
                for (Path entry : stream) {
                   
                    BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                    System.out.println("Processing file: " + entry.getFileName() + 
                                       " | Size: " + attrs.size() + " bytes" +
                                       " | Last Modified: " + attrs.lastModifiedTime());

                   
                    try (BufferedReader reader = Files.newBufferedReader(entry)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("  Log: " + line);
                            if (line.contains("DEPOSIT")) {
                                totalDeposits += extractAmount(line);
                            } else if (line.contains("WITHDRAW")) {
                                totalWithdrawals += extractAmount(line);
                            }
                        }
                    }
                }
            }
        }

        String reportContent = "=== END OF DAY SUMMARY REPORT ===\n" +
                               "Total Deposits: ₹" + totalDeposits + "\n" +
                               "Total Withdrawals: ₹" + totalWithdrawals + "\n";
        
        Files.write(reportFile, reportContent.getBytes());
        System.out.println("\nReport generated successfully at: " + reportFile);
        System.out.println(reportContent);
    }

    private static long extractAmount(String line) {
        try {
            String[] parts = line.split("₹");
            if (parts.length > 1) {
                return Long.parseLong(parts[1].trim());
            }
        } catch (NumberFormatException e) {
            
        }
        return 0;
    }
}
