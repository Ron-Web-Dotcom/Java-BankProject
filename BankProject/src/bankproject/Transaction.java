package bankproject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    public enum Type {
        DEPOSIT, WITHDRAWAL, INTEREST, SERVICE_CHARGE
    }

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_FMT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Type type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;

    /** Used for new, live transactions. */
    public Transaction(Type type, double amount, double balanceAfter) {
        this(type, amount, balanceAfter, LocalDateTime.now());
    }

    /** Used when loading transactions from a file. */
    public Transaction(Type type, double amount, double balanceAfter, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = timestamp;
    }

    /** Serialize to one line for accounts.dat */
    public String toFileLine() {
        return String.format("TX|%s|%.2f|%.2f|%s",
                type, amount, balanceAfter, timestamp.format(FILE_FMT));
    }

    /** Deserialize from a TX| line in accounts.dat. Returns null if the line is malformed. */
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

    @Override
    public String toString() {
        return String.format("%-20s  %-15s  %+10.2f   Balance: $%.2f",
                timestamp.format(DISPLAY_FMT), type, amount, balanceAfter);
    }
}
