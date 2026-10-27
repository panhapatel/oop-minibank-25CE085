package util;

import model.Account;
import java.io.*;
import java.nio.file.Path;

public class StatePersister {
    public static void save(Account[] accounts, Path file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
            oos.writeObject(accounts);
            System.out.println("Accounts successfully saved to " + file);
        }
    }

    public static Account[] load(Path file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
            Account[] accounts = (Account[]) ois.readObject();
            System.out.println("Accounts successfully loaded from " + file);
            return accounts;
        }
    }
}
