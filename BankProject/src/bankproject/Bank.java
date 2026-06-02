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

/**
 * Central bank registry — manages all accounts in memory and handles
 * reading from and writing to the {@value #DATA_FILE} persistence file.
 *
 * <p><b>Responsibilities:</b>
 * <ul>
 *   <li>Maintaining the in-memory list of {@link Account} objects</li>
 *   <li>Enforcing unique account numbers across the bank</li>
 *   <li>Executing atomic fund transfers between accounts</li>
 *   <li>Persisting and restoring the full account + transaction-history state</li>
 * </ul>
 *
 * <p><b>Persistence file format ({@code accounts.dat}):</b>
 * <pre>
 * ACCOUNT|CHECKING|Ron Taylor|1001|1250.00
 * TX|DEPOSIT|500.00|500.00|2024-03-14T10:30:00
 * TX|WITHDRAWAL|-150.00|1050.00|2024-03-14T11:00:00
 * ACCOUNT|SAVINGS|Jane Doe|1002|5016.67
 * TX|DEPOSIT|5000.00|5000.00|2024-03-14T09:00:00
 * TX|INTEREST|16.67|5016.67|2024-03-14T12:00:00
 * </pre>
 *
 * @author RON TAYLOR
 */
public class Bank {

    /** Name of the flat-file used to persist account and transaction data. */
    private static final String DATA_FILE = "accounts.dat";

    /** In-memory list of all accounts managed by this bank instance. */
    private final List<Account> accounts = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Account management
    // -------------------------------------------------------------------------

    /**
     * Registers a newly created account with the bank.
     *
     * @param account the account to add; its account number must be unique
     */
    public void addAccount(Account account) {
        accounts.add(account);
    }

    /**
     * Returns the live list of all accounts held by this bank.
     *
     * @return mutable list of accounts (modifications affect the bank's state)
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Checks whether a given account number is already in use.
     *
     * @param accountNumber the number to test
     * @return {@code true} if an account with that number already exists
     */
    public boolean isAccountNumberTaken(int accountNumber) {
        return accounts.stream().anyMatch(a -> a.getAccountNumber() == accountNumber);
    }

    /**
     * Looks up an account by its number.
     *
     * @param accountNumber the account number to search for
     * @return an Optional containing the matching account, or empty if not found
     */
    public Optional<Account> findAccount(int accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber() == accountNumber)
                .findFirst();
    }

    /**
     * Atomically transfers {@code amount} from {@code from} to {@code to}.
     * Both operations are recorded in the respective account histories via
     * the standard {@link Account#withdraw} and {@link Account#deposit} methods.
     *
     * @param from   the source account
     * @param to     the destination account
     * @param amount the amount to transfer; must be &gt; 0 and &lt;= from's balance
     * @return {@code true} on success; {@code false} if amount is invalid or
     *         the source has insufficient funds
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
     * Saves all accounts and their complete transaction histories to
     * {@value #DATA_FILE}, overwriting any existing file.
     *
     * <p>Each account is written as one {@code ACCOUNT|} header line followed
     * by one {@code TX|} line per transaction.
     *
     * @throws IOException if the file cannot be created or written
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
     * Loads all accounts and their transaction histories from {@value #DATA_FILE}.
     * Returns silently if the file does not exist (first run). Malformed lines
     * are skipped without throwing an exception.
     *
     * @throws IOException if the file exists but cannot be read
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

    /**
     * Parses the payload of an {@code ACCOUNT|} line and constructs the
     * appropriate account subclass.
     *
     * <p>Expected format: {@code TYPE|ownerName|accountNumber|balance}
     *
     * @param data the text after the {@code "ACCOUNT|"} prefix
     * @return the reconstructed Account, or {@code null} if malformed or type unknown
     */
    private Account parseAccountLine(String data) {
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
            // Corrupt line - skip it
        }
        return null;
    }
}
