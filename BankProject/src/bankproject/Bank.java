package bankproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Bank {

    private static final String DATA_FILE = "accounts.dat";

    private final List<Account> accounts = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Account management
    // -------------------------------------------------------------------------

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public boolean isAccountNumberTaken(int accountNumber) {
        return accounts.stream().anyMatch(a -> a.getAccountNumber() == accountNumber);
    }

    public Optional<Account> findAccount(int accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber() == accountNumber)
                .findFirst();
    }

    /**
     * Transfers {@code amount} from {@code from} to {@code to}.
     * Returns false if amount is invalid or from has insufficient funds.
     */
    public boolean transfer(Account from, Account to, double amount) {
        if (amount <= 0 || amount > from.getBalance()) return false;
        from.withdraw(amount);
        to.deposit(amount);
        return true;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    /**
     * Saves all accounts and their full transaction histories to accounts.dat.
     *
     * File format:
     *   ACCOUNT|CHECKING|Ron Taylor|1001|2500.00
     *   TX|DEPOSIT|2500.00|2500.00|2024-03-14T10:30:00
     *   TX|WITHDRAWAL|-200.00|2300.00|2024-03-14T11:00:00
     *   ACCOUNT|SAVINGS|Jane Doe|1002|15000.00
     *   TX|INTEREST|50.00|15050.00|2024-03-14T12:00:00
     */
    public void save() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Account account : accounts) {
                writer.println(account.toFileLine());
                for (String txLine : account.transactionFileLines()) {
                    writer.println(txLine);
                }
            }
        }
    }

    /**
     * Loads accounts (and their transaction histories) from accounts.dat.
     * Silently skips malformed lines.
     */
    public void load() throws IOException {
        if (!Files.exists(Paths.get(DATA_FILE))) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            Account current = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("ACCOUNT|")) {
                    current = parseAccountLine(line.substring("ACCOUNT|".length()));
                    if (current != null) accounts.add(current);

                } else if (line.startsWith("TX|") && current != null) {
                    Transaction tx = Transaction.fromFileLine(line);
                    if (tx != null) current.addHistoricalTransaction(tx);
                }
            }
        }
    }

    private Account parseAccountLine(String data) {
        // Format: TYPE|Name|accountNumber|balance
        String[] parts = data.split("\\|", 4);
        if (parts.length < 4) return null;
        try {
            String type    = parts[0];
            String name    = parts[1];
            int    accNum  = Integer.parseInt(parts[2].trim());
            double balance = Double.parseDouble(parts[3].trim());

            if ("CHECKING".equals(type)) return new CheckingAccount(name, accNum, balance, true);
            if ("SAVINGS".equals(type))  return new SavingsAccount(name, accNum, balance, true);
        } catch (NumberFormatException e) {
            // Corrupt line — skip it
        }
        return null;
    }
}
