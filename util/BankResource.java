package util;

public class BankResource implements AutoCloseable {
    public BankResource() {
        System.out.println("[Resource] Bank Secure Connection Opened.");
    }

    public void processSession() {
        System.out.println("[Resource] Processing secure banking session...");
    }

    @Override
    public void close() {
        System.out.println("[Resource] Bank Secure Connection Closed Automatically.");
    }
}
