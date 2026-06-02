package bankproject;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all bank account types.
 *
 * <p>Provides the core operations shared by every account:
 * deposits, withdrawals, a full timestamped transaction history, and
 * formatted statement printing. Subclasses implement
 * {@link #applyMonthlyFees()} and {@link #getAccountType()} to define
 * type-specific monthly fee or interest behaviour.
 *
 * <p><b>Persistence:</b> Accounts serialize themselves via
 * {@link #toFileLine()} and {@link #transactionFileLines()}, and restore
 * their history via {@link #addHistoricalTransaction(Transaction)}.
 *
 * @author RON TAYLOR
 * @see CheckingAccount
 * @see SavingsAccount
 */
public abstract class Account {

    /** The full name of the account holder. */
    protected final String ownerName;

    /** The unique account identifier. */
    protected final int accountNumber;

    /** The current account balance in dollars. */
    protected double balance;

    /** Chronological list of every transaction recorded against this account. */
    protected final List<Transaction> history = new ArrayList<>();

    /**
     * Opens a brand-new account. If {@code initialBalance} is greater than zero,
     * an opening DEPOSIT transaction is recorded automatically.
     *
     * @param ownerName      the full name of the account holder
     * @param accountNumber  the unique account number
     * @param initialBalance the opening balance; must be &gt;= 0
     */
    public Account(String ownerName, int accountNumber, double initialBalance) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        if (initialBalance > 0) {
            history.add(new Transaction(Transaction.Type.DEPOSIT, initialBalance, balance));
        }
    }

    /**
     * Loading constructor — restores an account from persisted data without
     * recording an opening deposit. Transaction history is restored separately
     * via {@link #addHistoricalTransaction(Transaction)}.
     *
     * @param ownerName     the full name of the account holder
     * @param accountNumber the unique account number
     * @param balance       the persisted current balance
     * @param loading       flag that distinguishes this from the new-account constructor
     */
    protected Account(String ownerName, int accountNumber, double balance, boolean loading) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    /**
     * Credits the specified amount to this account and records a DEPOSIT transaction.
     *
     * @param amount the amount to deposit; must be &gt; 0
     * @return {@code true} on success; {@code false} if amount is zero or negative
     */
    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        balance += amount;
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance));
        return true;
    }

    /**
     * Debits the specified amount from this account and records a WITHDRAWAL transaction.
     *
     * @param amount the amount to withdraw; must be &gt; 0 and &lt;= current balance
     * @return {@code true} on success; {@code false} if amount is invalid or exceeds balance
     */
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        history.add(new Transaction(Transaction.Type.WITHDRAWAL, -amount, balance));
        return true;
    }

    /**
     * Applies this account's monthly fees or interest.
     *
     * <ul>
     *   <li>{@link CheckingAccount}: deducts a $50 service charge when balance &lt; $1,000</li>
     *   <li>{@link SavingsAccount}: credits 4% annual interest divided over 12 months</li>
     * </ul>
     */
    public abstract void applyMonthlyFees();

    /**
     * Returns the display label for this account type.
     *
     * @return e.g. {@code "Checking"} or {@code "Savings"}
     */
    public abstract String getAccountType();

    // -------------------------------------------------------------------------
    // File persistence
    // -------------------------------------------------------------------------

    /**
     * Serializes this account's header to a pipe-delimited line for {@code accounts.dat}.
     *
     * <p>Format: {@code ACCOUNT|TYPE|ownerName|accountNumber|balance}
     * <br>Example: {@code ACCOUNT|CHECKING|Ron Taylor|1001|1250.00}
     *
     * @return the serialized account header line
     */
    public String toFileLine() {
        return String.format("ACCOUNT|%s|%s|%d|%.2f",
                getAccountType().toUpperCase(), ownerName, accountNumber, balance);
    }

    /**
     * Serializes every transaction in history to text lines for {@code accounts.dat}.
     *
     * @return ordered list of serialized TX lines
     */
    public List<String> transactionFileLines() {
        List<String> lines = new ArrayList<>();
        for (Transaction tx : history) {
            lines.add(tx.toFileLine());
        }
        return lines;
    }

    /**
     * Appends a transaction loaded from file to the history without mutating the balance.
     * The balance is already restored from the account header line.
     *
     * @param tx the historical transaction to append
     */
    public void addHistoricalTransaction(Transaction tx) {
        history.add(tx);
    }

    // -------------------------------------------------------------------------
    // Display
    // -------------------------------------------------------------------------

    /**
     * Prints a full formatted account statement to standard output, including
     * owner, account type, current balance, and every recorded transaction
     * in chronological order.
     */
    public void printStatement() {
        System.out.println("\n========================================");
        System.out.println("           Account Statement            ");
        System.out.println("========================================");
        System.out.printf("  Owner:   %s%n", ownerName);
        System.out.printf("  Account: #%d  (%s)%n", accountNumber, getAccountType());
        System.out.printf("  Balance: $%.2f%n", balance);
        System.out.println("----------------------------------------");
        System.out.printf("%-20s  %-15s  %10s   %s%n",
                "Date/Time", "Type", "Amount", "Balance");
        System.out.println("----------------------------------------");
        if (history.isEmpty()) {
            System.out.println("  No transactions recorded.");
        } else {
            history.forEach(System.out::println);
        }
        System.out.println("========================================\n");
    }

    /** @return the current balance in dollars */
    public double getBalance()    { return balance; }

    /** @return the account holder's full name */
    public String getOwnerName()  { return ownerName; }

    /** @return the unique account number */
    public int getAccountNumber() { return accountNumber; }
}
