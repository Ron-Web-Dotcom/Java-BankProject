package bankproject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single financial transaction recorded against a bank account.
 *
 * <p>Each transaction captures the operation type, monetary amount, running
 * balance after the operation, and the exact timestamp. Transactions are
 * immutable and can be serialized to / deserialized from a pipe-delimited
 * text line for storage in {@code accounts.dat}.
 *
 * <p>Supported types (see {@link Type}):
 * DEPOSIT, WITHDRAWAL, INTEREST, SERVICE_CHARGE.
 *
 * @author RON TAYLOR
 */
public class Transaction {

    /**
     * The category of a financial operation.
     *
     * <ul>
     *   <li>DEPOSIT        - funds added to the account</li>
     *   <li>WITHDRAWAL     - funds removed from the account</li>
     *   <li>INTEREST       - interest credited (Savings accounts)</li>
     *   <li>SERVICE_CHARGE - fee deducted when balance falls below minimum (Checking accounts)</li>
     * </ul>
     */
    public enum Type {
        DEPOSIT, WITHDRAWAL, INTEREST, SERVICE_CHARGE
    }

    /** Formatter used when printing the statement to the console. */
    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Formatter used when writing to / reading from accounts.dat. */
    private static final DateTimeFormatter FILE_FMT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;

    /**
     * Creates a new live transaction timestamped to the current moment.
     *
     * @param type         the category of the operation
     * @param amount       the monetary amount; positive for credits, negative for debits
     * @param balanceAfter the account balance immediately after this transaction
     */
    public Transaction(Type type, double amount, double balanceAfter) {
        this(type, amount, balanceAfter, LocalDateTime.now());
    }

    /**
     * Creates a transaction with an explicit timestamp.
     * Used when restoring transactions from the persistence file.
     *
     * @param type         the category of the operation
     * @param amount       the monetary amount; positive for credits, negative for debits
     * @param balanceAfter the account balance immediately after this transaction
     * @param timestamp    the original date/time of the transaction
     */
    public Transaction(Type type, double amount, double balanceAfter, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = timestamp;
    }

    /**
     * Serializes this transaction to one pipe-delimited line for {@code accounts.dat}.
     *
     * <p>Format: {@code TX|TYPE|amount|balanceAfter|ISO-timestamp}
     *
     * @return the serialized line, e.g. {@code TX|DEPOSIT|500.00|1500.00|2024-03-14T10:30:00}
     */
    public String toFileLine() {
        return String.format("TX|%s|%.2f|%.2f|%s",
                type, amount, balanceAfter, timestamp.format(FILE_FMT));
    }

    /**
     * Deserializes a {@code TX|} line from {@code accounts.dat} into a Transaction.
     *
     * @param line the raw text line from the file
     * @return the reconstructed Transaction, or {@code null} if the line is malformed
     */
    public static Transaction fromFileLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 5) return null;
        try {
            Type type = Type.valueOf(parts[1]);
            double amount = Double.parseDouble(parts[2]);
            double balanceAfter = Double.parseDouble(parts[3]);
            LocalDateTime timestamp = LocalDateTime.parse(parts[4], FILE_FMT);
            return new Transaction(type, amount, balanceAfter, timestamp);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns a formatted one-line statement entry showing date/time, type,
     * amount, and running balance.
     *
     * @return human-readable transaction summary
     */
    @Override
    public String toString() {
        return String.format("%-20s  %-15s  %+10.2f   Balance: $%.2f",
                timestamp.format(DISPLAY_FMT), type, amount, balanceAfter);
    }
}
